package com.qiscus.meet

import android.content.Context

/**
 * Created on : 11/03/20
 * Author     : arioki
 * Name       : Yoga Setiawan
 * GitHub     : https://github.com/arioki
 */
object MeetSharePref {
    private var sharePreference =
        QiscusMeet.application.getSharedPreferences("qiscus_meet_", Context.MODE_PRIVATE)

    fun getPref() = sharePreference

    private fun getEditor() = getPref().edit()

    fun setRoomId(roomId: String) = getEditor().putString("room_id", roomId).commit()

    fun getRoomId() = getPref().getString("room_id", "") ?: ""

    fun setType(type: String) = getEditor().putString("call_type", type).commit()

    fun getType() = getPref().getString("call_type", "") ?: ""
}