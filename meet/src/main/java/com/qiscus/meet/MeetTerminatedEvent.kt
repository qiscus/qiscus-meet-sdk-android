package com.qiscus.meet

/**
 * Created on : 2019-09-27
 * Author     : Taufik Budi S
 * Github     : https://github.com/tfkbudi
 */
data class MeetTerminatedConfEvent(
    val roomId: String,
    val data: MutableMap<String, Any>?,
    val type: QiscusMeet.Type?
)