package com.qiscus.meet

import android.content.Context
import android.content.Intent
import android.util.Log
import com.qiscus.meet.model.Call
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

/**
 * Created on : 14/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class MeetInfo {
    private var roomId: String = ""
    private var calleeUsername: String = ""
    private var calleeDisplayName: String = ""
    private var calleeAvatar: String = ""
    private var callerUsername: String = ""
    private var callerDisplayName = ""
    private var callerAvatar: String = ""
    private var callType: QiscusMeet.CallType = QiscusMeet.CallType.VIDEO
    private var callAs: QiscusMeet.CallAs = QiscusMeet.CallAs.CALLER


    fun setRoomId(roomId: String) = apply { this.roomId = roomId }

    fun setCalleeUsername(calleeUsername: String) = apply { this.calleeUsername = calleeUsername }

    fun setCalleeDisplayName(calleeUsername: String) = apply { this.calleeDisplayName = calleeDisplayName }

    fun setCalleeAvatar(calleeAvatar: String) = apply { this.calleeAvatar = calleeAvatar }

    fun setCallerUsername(callerUsername: String) = apply { this.callerUsername = callerUsername }

    fun setCallerDisplayName(callerDisplayName: String) = apply { this.callerDisplayName = callerDisplayName }

    fun setCallerAvatar(callerAvatar: String) = apply { this.callerAvatar = callerAvatar }

    fun setCallType(callType: QiscusMeet.CallType) = apply { this.callType = callType }

    fun setCallAs(callAs: QiscusMeet.CallAs) = apply { this.callAs = callAs }

    fun build(context: Context) {
        if(callAs == QiscusMeet.CallAs.CALLEE) {
            val call = Call(roomId,calleeUsername, calleeDisplayName, calleeAvatar,
                callerUsername, callerDisplayName, callerAvatar, callType, callAs)
            val intent = IncomingCallActivity.generateIntent(context, call)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } else {
            val options = JitsiMeetConferenceOptions.Builder()
                    .setRoom(roomId)
                    .build()

            QiscusMeetActivity.launch(context, options)
        }
    }
}