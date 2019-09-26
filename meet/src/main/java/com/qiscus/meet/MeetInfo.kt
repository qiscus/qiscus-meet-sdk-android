package com.qiscus.meet

import android.content.Context
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONObject

/**
 * Created on : 14/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class MeetInfo {

    private var roomId: String = ""
    private var type: QiscusMeet.Type = QiscusMeet.Type.VIDEO
    private var displayName: String = "Guest"
    private var avatar: String? = null

    fun setRoomId(roomId: String) = apply { this.roomId = roomId }

    fun setType(type: QiscusMeet.Type) = apply { this.type = type }

    fun setDisplayName(displayName: String) = apply { this.displayName = displayName }

    fun setAvatar(avatar: String) = apply { this.avatar = avatar }

    fun build(context: Context) {
        val token = generateToken(displayName, avatar)

        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(roomId)
            .setAudioOnly(type == QiscusMeet.Type.VOICE)
            .setToken(token)
            .build()

        QiscusMeetActivity.launch(context, options, roomId)
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