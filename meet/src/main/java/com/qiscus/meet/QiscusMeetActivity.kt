package com.qiscus.meet

import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jitsi.meet.sdk.*
import org.jitsi.meet.sdk.R


/**
 * Created on : 21/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class QiscusMeetActivity : JitsiMeetActivity() {

    val TAG = this.javaClass.simpleName

    companion object {
        fun launch(context: Context, options: JitsiMeetConferenceOptions?) {
            val intent = Intent(context, QiscusMeetActivity::class.java)
            intent.action = JitsiMeetActivity.ACTION_JITSI_MEET_CONFERENCE
            intent.putExtra(JitsiMeetActivity.JITSI_MEET_CONFERENCE_OPTIONS, options)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    protected fun getQiscusView(): JitsiMeetView? {
        val fragment = this.supportFragmentManager.findFragmentById(R.id.jitsiFragment) as JitsiMeetFragment?
        return fragment?.jitsiView
    }

    @Subscribe
    fun onReceivedEvent(meetEvent : MeetEvent) {
        if(meetEvent.event == QiscusMeet.QiscusMeetEvent.REJECTED) {
            jitsiView?.leave()
        }
    }

    override fun join(options: JitsiMeetConferenceOptions?) {
        super.join(options)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}