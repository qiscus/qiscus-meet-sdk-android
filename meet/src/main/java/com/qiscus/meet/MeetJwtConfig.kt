package com.qiscus.meet

import org.json.JSONObject

class MeetJwtConfig {
    private var appId: String = QiscusMeet.getAppID()
    private var email: String = ""
    private var iss: String = "meetcall" // TODO move to constant place as default value
    private var sub: String = QiscusMeet.getURL().host
    private var moderator: Boolean = false
    private lateinit var jwtPayload: JSONObject

    fun setEmail(email: String) = apply { this.email = email }
    fun setIss(iss: String) = apply { this.iss = iss }
    fun setSub(sub: String) = apply { this.sub = sub }
    fun setModerator(enableModerator: Boolean) = apply { this.moderator = enableModerator }

    fun getJwtPayload(): JSONObject {
        return jwtPayload
    }

    fun build() {
        jwtPayload = JSONObject()
        jwtPayload.put("appId", appId)
        jwtPayload.put("email", email)
        jwtPayload.put("moderator", moderator)
        jwtPayload.put("iss", iss)
        jwtPayload.put("sub", sub)
    }

}