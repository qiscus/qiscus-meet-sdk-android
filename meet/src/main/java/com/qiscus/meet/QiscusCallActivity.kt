package com.qiscus.meet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.modules.core.PermissionListener
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetViewListener


class QiscusCallActivity : AppCompatActivity(), JitsiMeetActivityInterface {
    lateinit var roomId: String

    companion object {
        val room = "roomid"
        const val ACTION_JITSI_MEET_CONFERENCE = "org.jitsi.meet.CONFERENCE"
        const val JITSI_MEET_CONFERENCE_OPTIONS = "JitsiMeetConferenceOptions"

        fun launch(context: Context, options: JitsiMeetConferenceOptions?, roomid: String) {
            val intent = Intent(context, QiscusCallActivity::class.java)
            intent.action = ACTION_JITSI_MEET_CONFERENCE
            intent.putExtra(JITSI_MEET_CONFERENCE_OPTIONS, options)
            intent.putExtra(room, roomid)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (MeetHolder.getJitsiView() == null) {
            val options =
                intent.getParcelableExtra<Parcelable>("JitsiMeetConferenceOptions") as? JitsiMeetConferenceOptions
            roomId = intent.getStringExtra(room)
            MeetHolder().initialise(this)
            if (options == null) {
                finish()
            }
            options?.let {
                MeetHolder().requestCallStart(roomId, it)
            }
        }

        MeetHolder.getJitsiView()?.let {
            val parent = it.parent as? ViewGroup
            parent?.removeView(it)
            setContentView(it)
            it.listener = object : JitsiMeetViewListener {
                override fun onConferenceTerminated(p0: MutableMap<String, Any>?) {
                    stopCall()
                }

                override fun onConferenceJoined(p0: MutableMap<String, Any>?) {
                    onJoined()
                }

                override fun onConferenceWillJoin(p0: MutableMap<String, Any>?) {
                    onJoined()
                }
            }

        }

    }

    private fun onJoined() {
        runOnUiThread {
            MeetHolder().createNotification(this)
        }
    }

    private fun stopCall() {
        runOnUiThread {
            MeetHolder().stopCall()
        }
    }


    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
