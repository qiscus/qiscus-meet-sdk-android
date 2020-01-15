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
import java.net.URL

/**
 * Created on : 14/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class MeetInfo(url: String) {

    private var roomId: String = ""
    private var type: QiscusMeet.Type = QiscusMeet.Type.VIDEO
    private var displayName: String = "Guest"
    private var avatar: String? = null
    private var url: String = url

    fun setRoomId(roomId: String) = apply { this.roomId = roomId }

    fun setTypeCall(type: QiscusMeet.Type) = apply { this.type = type }

    fun setDisplayName(displayName: String) = apply { this.displayName = displayName }

    fun setAvatar(avatar: String) = apply { this.avatar = avatar }

    fun build(context: Context) {
        val token = generateToken(displayName, avatar)


        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url+"/get-room-size?room=" + roomId)
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
                    if (jsonObject["participants"] == "0") {
                        Log.d("QiscusMeet", "You haven't participants")
                    } else {
                        val options = JitsiMeetConferenceOptions.Builder()
                            .setRoom(roomId)
                            .setAudioOnly(type == QiscusMeet.Type.VOICE)
                            .setToken(token)
                            .build()

                        QiscusMeetActivity.launch(context, options, roomId)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
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