package com.qiscus.meet

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetFragment
import org.jitsi.meet.sdk.JitsiMeetView


/**
 * Created on : 21/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class QiscusMeetActivity : JitsiMeetActivity() {

    val TAG = this.javaClass.simpleName
    lateinit var roomId: String


    companion object {
        val room = "roomid"
        const val ACTION_JITSI_MEET_CONFERENCE = "org.jitsi.meet.CONFERENCE"
        const val JITSI_MEET_CONFERENCE_OPTIONS = "JitsiMeetConferenceOptions"

        fun launch(context: Context, options: JitsiMeetConferenceOptions?, roomid: String) {
            val intent = Intent(context, QiscusMeetActivity::class.java)
            intent.action = ACTION_JITSI_MEET_CONFERENCE
            intent.putExtra(JITSI_MEET_CONFERENCE_OPTIONS, options)
            intent.putExtra(room, roomid)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        roomId = intent.getStringExtra(room)
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

    override fun join(options: JitsiMeetConferenceOptions?) {
        super.join(options)
    }

    override fun onConferenceTerminated(data: MutableMap<String, Any>?) {
        super.onConferenceTerminated(data)

        EventBus.getDefault().post(MeetTerminatedConfEvent(roomId, data))
        val intent = Intent(this, CreateNotfication::class.java)
        this.stopService(intent)
    }

    override fun onConferenceJoined(data: MutableMap<String, Any>?) {
        super.onConferenceJoined(data)

        val intent = Intent(this, CreateNotfication::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(0)
    }

}