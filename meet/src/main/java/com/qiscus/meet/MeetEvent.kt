package com.qiscus.meet

/**
 * Created on : 26/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
data class MeetEvent(val roomId: String, val event: QiscusMeet.QiscusMeetEvent)

data class MeetEventBackPressedConference(val data: MutableMap<String, Any>?)
data class MeetEventCustom(val data: MutableMap<String, Any>?)
data class MeetTerminatedConferenceEvent(
    val roomId: String?,
    val data: MutableMap<String, Any>?,
    val type: QiscusMeet.Type?
)