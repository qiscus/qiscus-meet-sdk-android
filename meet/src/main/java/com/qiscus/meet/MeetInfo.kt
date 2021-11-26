@file:Suppress("SENSELESS_COMPARISON")

package com.qiscus.meet

import android.content.Context
import android.util.Log
import okhttp3.*
import org.jitsi.meet.sdk.BuildConfig
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.net.URL

/**
 * Created on : 14/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class MeetInfo(url: String, typeCaller: QiscusMeet.TypeCaller, config: MeetConfig) {

    private var roomId: String = ""
    private var type: QiscusMeet.Type = QiscusMeet.Type.VIDEO
    private var displayName: String = "Guest"
    private var avatar: String? = null
    private var url: String = url
    private var typeCaller: QiscusMeet.TypeCaller = typeCaller
    private var muted: Boolean = false
    private var config: MeetConfig = config
    fun setRoomId(roomId: String) = apply { this.roomId = roomId }

    fun setTypeCall(type: QiscusMeet.Type) = apply { this.type = type }

    fun setDisplayName(displayName: String) = apply { this.displayName = displayName }

    fun setAvatar(avatar: String) = apply { this.avatar = avatar }

    fun setMuted(muted: Boolean) = apply { this.muted = muted }

    fun build(context: Context) {
        generateToken(context, displayName, avatar)
    }


    private fun call(context: Context, appId: String, token: String) {
        if (typeCaller.equals(QiscusMeet.TypeCaller.CALLER)) {
            val objectPayload = config.getJwtConfig().getJwtPayload()
            val roomUrl: String = "$appId/$roomId"
            val userInfo = JitsiMeetUserInfo()
            userInfo.avatar = URL(avatar)
            userInfo.displayName = displayName
            userInfo.email = objectPayload.get("email").toString()
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(roomUrl)
                .setAudioMuted(muted)
                .setAudioOnly(type == QiscusMeet.Type.VOICE || type == QiscusMeet.Type.CONFERENCE)
                .setFeatureFlag("pip.enabled", true)
                .setFeatureFlag("requirepassword.enabled", config.getPassword())
                .setFeatureFlag("chat.enabled", config.getChat())
                .setFeatureFlag("overflow-menu.enabled", config.getOverflowMenu())
                .setFeatureFlag("videoThumbnail.enabled", config.getVideoThumbnailsOn())
                .setFeatureFlag("meeting-name.enabled", config.isEnableRoomName())
                .setFeatureFlag("android.screensharing.enabled", config.getScreenSharing())
                .setFeatureFlag("recording.enabled", config.getRecording())
                .setFeatureFlag("reactions.enabled", config.isEnableReactions())
                .setFeatureFlag("raise-hand.enabled", config.isEnableRaiseHand())
                .setFeatureFlag("security-options.enabled", config.isEnableSecurityOptions())
                .setFeatureFlag("toolbox.enabled", config.isEnableToolbox())
                .setFeatureFlag("toolbox.alwaysVisible", config.toolboxAlwaysVisible())
                .setFeatureFlag("tile-view.enabled", config.isEnableTileView())
                .setFeatureFlag("participantMenu.enabled", config.isEnableParticipantMenu())
                .setFeatureFlag("videoMuteButton.enabled", config.isEnableVideoMuteButton())
                .setFeatureFlag("audioMuteButton.enabled", config.isEnableAudioMuteButton())
                .setToken(token)
                .setUserInfo(userInfo)
                .setVideoMuted(false)
            if (config.getAutoRecording()) {
                options.setFeatureFlag("autoRecording.enabled", config.getAutoRecording())
                getServerRecording(appId, context, options)
            } else {
                QiscusMeetActivity.launch(
                    context,
                    options.build(),
                    roomUrl,
                    config.getEnableBackPress()
                )
            }
        } else {
            val client = OkHttpClient()
            val request: Request = Request.Builder()
                .url(url + "/get-room-size?room=" + roomId)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("QiscusMeet", "Failed get participants")
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    assert(response.body() != null)
                    val jsonData = response.body()!!.string()
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val totalParticipants: Int = jsonObject["participants"] as Int

                        if (totalParticipants > 0) {
                            val options = JitsiMeetConferenceOptions.Builder()
                                .setRoom(roomId)
                                .setVideoMuted(false)
                                .setAudioOnly(type == QiscusMeet.Type.VOICE)
                                .setToken(token)
                                .build()
                            QiscusMeetActivity.launch(
                                context,
                                options,
                                roomId,
                                config.getEnableBackPress()
                            )
                        } else {
                            Log.d("QiscusMeet", "You haven't participants")
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
        }
    }


    private fun generateToken(context: Context, name: String, avatar: String?) {
        val JSON = MediaType.parse("application/json; charset=utf-8")

        val objectPayload = config.getJwtConfig().getJwtPayload()

        objectPayload.put("name", name)
        objectPayload.put("displayName", name)
        objectPayload.put("room", roomId)
        if (avatar == null) {
            objectPayload.put("avatar", "")
        } else {
            objectPayload.put("avatar", avatar)
        }


        val client = OkHttpClient()
        val body: RequestBody = RequestBody.create(JSON, objectPayload.toString())
        val request: Request = Request.Builder()
            .url("$url:5050/generate_url")
            .post(body)
            .addHeader("Authorization", "Bearer X6tMDYkJF7MVPQ32")
            .addHeader("content-type", "application/json; charset=utf-8")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.d("Failed get jwt")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (BuildConfig.DEBUG && response.body() == null) {
                    error("Assertion failed")
                }
                if (response.code() == 200) {
                    val jsonData = response.body()!!.string()
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val token: String = jsonObject["token"] as String
                        call(context, objectPayload.get("app_id") as String, token)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Log.d("FAILED", "Response Code: ${response.code()} ${response.message()}")
                }
            }
        })
    }


    private fun getServerRecording(
        appId: String,
        context: Context,
        options: JitsiMeetConferenceOptions.Builder
    ) {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url("$url:5050/api/recordings/${appId}/status")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Timber.d("Failed get jwt")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.code() == 200) {
                    val jsonData = response.body()!!.string()
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val recordingEvent = RecordingEvent()
                        recordingEvent.status = jsonObject["status"] as String ?: "null"
                        recordingEvent.total_quota = jsonObject["total_quota"] as Int ?: 0
                        recordingEvent.remaining_quota = jsonObject["remaining_quota"] as Int ?: 0
                        QiscusMeetActivity.launchWithRecording(
                            context,
                            options.build(),
                            roomId,
                            config.getEnableBackPress(),
                            recordingEvent
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else if (response.code() == 400) {
                    val jsonData = response.body()!!.string()
                    try {
                        val jsonObject = JSONObject(jsonData)
                        val recordingEvent = RecordingEvent()
                        recordingEvent.message = jsonObject["message"] as String ?: "null"
                        recordingEvent.status = jsonObject["status"] as String ?: "null"
                        QiscusMeetActivity.launchWithRecording(
                            context,
                            options.build(),
                            roomId,
                            config.getEnableBackPress(),
                            recordingEvent
                        )
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    Log.d("FAILED", "Response Code: ${response.code()} ${response.message()}")

                }
            }
        })
    }
}