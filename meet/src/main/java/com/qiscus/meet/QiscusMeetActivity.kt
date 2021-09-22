package com.qiscus.meet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetFragment
import org.jitsi.meet.sdk.JitsiMeetView
import java.util.HashMap


/**
 * Created on : 21/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class QiscusMeetActivity : JitsiMeetActivity() {

    val TAG = this.javaClass.simpleName
    lateinit var roomId: String
    var enableBackpressed:Boolean = true

    companion object {
        val room = "roomid"
        val backpressed = "backpressed"
        const val ACTION_JITSI_MEET_CONFERENCE = "org.jitsi.meet.CONFERENCE"
        const val JITSI_MEET_CONFERENCE_OPTIONS = "JitsiMeetConferenceOptions"
        var activity: QiscusMeetActivity? = null
        fun launch(context: Context, options: JitsiMeetConferenceOptions?, roomid: String, enableBackpressed: Boolean) {
            val intent = Intent(context, QiscusMeetActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.action = ACTION_JITSI_MEET_CONFERENCE
            intent.putExtra(JITSI_MEET_CONFERENCE_OPTIONS, Gson().toJson(options))
            intent.putExtra(room, roomid)
            intent.putExtra(backpressed,enableBackpressed)
            context.startActivity(intent)
        }

        fun endCall() {
                MeetHolder.getJitsiView()?.leave()
                MeetHolder.removeJitsiView()
            val typeCall: QiscusMeet.Type = MeetSharePref.getType().let {
                when (it) {
                    QiscusMeet.Type.CONFERENCE.toString() -> QiscusMeet.Type.CONFERENCE
                    QiscusMeet.Type.VOICE.toString() -> QiscusMeet.Type.VOICE
                    QiscusMeet.Type.VIDEO.toString() -> QiscusMeet.Type.VIDEO
                    else -> QiscusMeet.Type.CONFERENCE
                }
            }
            EventBus.getDefault()
                .post(
                    MeetTerminatedConfEvent(
                        MeetSharePref.getRoomId(),
                        null,
                        typeCall
                    )
                )
            activity?.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        roomId = intent.getStringExtra(room)!!
        enableBackpressed = intent.getBooleanExtra(backpressed,true)
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    protected fun getQiscusView(): JitsiMeetView? {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.jitsiFragment) as JitsiMeetFragment?
        return fragment?.jitsiView
    }

    @Subscribe
    fun onReceivedEvent(meetEvent: MeetEvent) {
        if (meetEvent.event == QiscusMeet.QiscusMeetEvent.REJECTED && meetEvent.roomId == roomId) {
            jitsiView?.leave()
        }
    }

    override fun initialize() {
        val meetOptions = Gson().fromJson(
            intent.getStringExtra(JITSI_MEET_CONFERENCE_OPTIONS),
            JitsiMeetConferenceOptions::class.java
        )
        this.join(meetOptions)
    }

    override fun onConferenceTerminated(extraData: HashMap<String, Any>?) {
        super.onConferenceTerminated(extraData)
                EventBus.getDefault().post(MeetTerminatedConfEvent(roomId, extraData, null))
//        val intent = Intent(this, CreateNotfication::class.java)
//        this.stopService(intent)
    }

    override fun onConferenceJoined(extraData: HashMap<String, Any>?) {
        super.onConferenceJoined(extraData)
    }

    override fun onParticipantJoined(extraData: HashMap<String, Any>?) {
        super.onParticipantJoined(extraData)
        EventBus.getDefault().post(MeetParticipantJoinedEvent(roomId,extraData,null))
    }

    override fun onParticipantLeft(extraData: HashMap<String, Any>?) {
        super.onParticipantLeft(extraData)
        EventBus.getDefault().post(MeetParticipantLeftEvent(roomId,extraData,null))
    }
//    override fun onConferenceTerminated(data: MutableMap<String, Any>?) {
//        super.onConferenceTerminated(data)
//        EventBus.getDefault().post(MeetTerminatedConfEvent(roomId, data, null))
////        val intent = Intent(this, CreateNotfication::class.java)
//        this.stopService(intent)
//    }
//
//    override fun onConferenceJoined(data: MutableMap<String, Any>?) {
//        super.onConferenceJoined(data)
//
////        val intent = Intent(this, CreateNotfication::class.java)
//        ContextCompat.startForegroundService(this, intent)
//    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(0)
    }

    override fun onBackPressed() {
        if (enableBackpressed){
            super.onBackPressed()
        }
    }
}