package com.qiscus.meet

/**
 * Created on : 2021-05-05
 * Author     : Gustu Maulana F
 * Email     : gustumaulanaf@gmail.com // gustu@qiscus.net
 */
data class MeetParticipantJoinedEvent(
    val roomId: String?,
    val data: MutableMap<String, Any>?,
    val type: QiscusMeet.Type?
)