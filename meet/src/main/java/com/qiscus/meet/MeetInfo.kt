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
     var options = Options()
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
//            val objectPayload = config.getJwtConfig().getJwtPayload()
            val roomUrl: String = "$appId/$roomId"

            options.roomURL = roomUrl
            options.audioMuted = muted
            options.audioOnly = type == QiscusMeet.Type.VOICE || type == QiscusMeet.Type.CONFERENCE
            options.pipEnabled = true
            options.requiredPasswordEnabled = config.getPassword()
            options.chatEnabled = config.getChat()
            options.overFlowMenuEnabled = config.getOverflowMenu()
            options.videoThumbnailEnabled = config.getVideoThumbnailsOn()
            options.meetingNameEnabled = config.isEnableRoomName()
            options.androidScreenSharingEnabled = config.getScreenSharing()
            options.recordingEnabled = config.getRecording()
            options.reactionsEnabled = config.isEnableReactions()
            options.raiseHandEnabled = config.isEnableRaiseHand()
            options.securityOptionsEnabled = config.isEnableSecurityOptions()
            options.toolboxEnabled = config.isEnableToolbox()
            options.toolboxAlwaysVisible = config.isEnableTileView()
            options.participantMenuEnabled = config.isEnableParticipantMenu()
            options.videoMuteButtonEnabled = config.isEnableVideoMuteButton()
            options.audioMuteButtonEnabled = config.isEnableAudioMuteButton()
            options.token = token
            options.videoMuted = false
            if (config.getAutoRecording()) {
                options.autoRecordingEnabled = config.getAutoRecording()
                getServerRecording(appId, context, options)
            } else {
                QiscusMeetActivity.launch(
                    context,
                    options,
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
                            options.roomURL = roomId
                            options.videoMuted = false
                            options.audioOnly = type == QiscusMeet.Type.VOICE
                            options.token = token
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
        options: Options
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
                            options,
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
                            options,
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