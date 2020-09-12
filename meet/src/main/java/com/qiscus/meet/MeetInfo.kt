package com.qiscus.meet

import android.content.Context
import android.util.Log
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import okhttp3.*
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

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
    private var config:MeetConfig = config

    fun setRoomId(roomId: String) = apply { this.roomId = roomId }

    fun setTypeCall(type: QiscusMeet.Type) = apply { this.type = type }

    fun setDisplayName(displayName: String) = apply { this.displayName = displayName }

    fun setAvatar(avatar: String) = apply { this.avatar = avatar }

    fun setMuted(muted: Boolean) = apply { this.muted = muted }

    fun build(context: Context) {
        val token = generateToken(displayName, avatar)


        if (typeCaller.equals(QiscusMeet.TypeCaller.CALLER)) {
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(roomId)
                .setAudioMuted(muted)
                .setAudioOnly(type == QiscusMeet.Type.VOICE || type == QiscusMeet.Type.CONFERENCE)
                .setFeatureFlag("pip.enabled", true)
                .setFeatureFlag("requirepassword.enabled", config.getPassword())
                .setFeatureFlag("chat.enabled", config.getChat())
                .setFeatureFlag("overflowMenu.enabled", config.getOverflowMenu())
                .setFeatureFlag("videoThumbnail.enabled", config.getVideoThumbnailsOn())
                .setToken(token)
                .build()
             QiscusMeetActivity.launch(context, options, roomId)
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
                                .setAudioOnly(type == QiscusMeet.Type.VOICE)
                                .setToken(token)
                                .build()

                            QiscusMeetActivity.launch(context, options, roomId)
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

    private fun generateToken(name: String, avatar: String?): String {
        val key = Keys.keyPairFor(SignatureAlgorithm.RS256)

        val claim = JSONObject()
        val user = JSONObject()
        user.put("name", name)
        avatar?.let { user.put("avatar", it) }
        claim.put("user", user)

        val token = Jwts.builder()
            .setHeaderParam("kid", "jitsi/custom_key_name")
            .claim("context", claim)
            .signWith(key.private, SignatureAlgorithm.RS256)
            .compact()

        return token
    }

}