package com.qiscus.meet

import android.content.Context
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

/**
 * Created on : 14/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class MeetInfo {

    private var roomId: String = ""
    private var type: QiscusMeet.Type = QiscusMeet.Type.VIDEO


    fun setRoomId(roomId: String) = apply { this.roomId = roomId }

    fun setType(type: QiscusMeet.Type) = apply { this.type = type }

    fun build(context: Context) {
            val options = JitsiMeetConferenceOptions.Builder()
                .setRoom(roomId)
                .setAudioOnly(type == QiscusMeet.Type.VOICE)
                .build()

        QiscusMeetActivity.launch(context, options, roomId)
    }

}