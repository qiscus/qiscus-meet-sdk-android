package com.qiscus.meet

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.facebook.react.modules.core.PermissionListener
import org.greenrobot.eventbus.EventBus
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate
import org.jitsi.meet.sdk.JitsiMeetActivityInterface
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetViewListener


class QiscusCallActivity : AppCompatActivity(), JitsiMeetActivityInterface {
    var roomId: String? = null
    var type: QiscusMeet.Type? = null

    companion object {
        private const val QISCUS_MEET_CONFERENCE_ROOM_ID = "roomId"
        private const val QISCUS_MEET_CONFERENCE_ACTION = "QiscusMeet.CONFERENCE"
        private const val QISCUS_MEET_CONFERENCE_OPTIONS = "QiscusMeetConferenceOptions"
        private const val QISCUS_MEET_CONFERENCE_TYPE = "QiscusConferenceType"

        fun launch(
            context: Context,
            options: JitsiMeetConferenceOptions?,
            roomId: String,
            type: QiscusMeet.Type
        ) {
            val intent = Intent(context, QiscusCallActivity::class.java)
            intent.action = QISCUS_MEET_CONFERENCE_ACTION
            intent.putExtra(QISCUS_MEET_CONFERENCE_OPTIONS, options)
            intent.putExtra(QISCUS_MEET_CONFERENCE_TYPE, type)
            intent.putExtra(QISCUS_MEET_CONFERENCE_ROOM_ID, roomId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1);
        if (MeetHolder.getJitsiView() == null) {

            val options =
                intent.getParcelableExtra<Parcelable>(QISCUS_MEET_CONFERENCE_OPTIONS) as? JitsiMeetConferenceOptions
            roomId = intent.getStringExtra(QISCUS_MEET_CONFERENCE_ROOM_ID)
            type =
                intent.getSerializableExtra(QISCUS_MEET_CONFERENCE_TYPE) as? QiscusMeet.Type
            MeetHolder().initialise(this)
            roomId?.let {
                MeetSharePref.setRoomId(it)
            }
            type?.let {
                MeetSharePref.setType(it.toString())
            }
            if (options == null) {
                finish()
            }
            options?.let {
                MeetHolder().requestCallStart(it)
            }
        }

        MeetHolder.getJitsiView()?.let {
            val parent = it.parent as? ViewGroup
            parent?.removeView(it)
            setContentView(it)
            it.listener = object : JitsiMeetViewListener {
                override fun onBackPressedConference(p0: MutableMap<String, Any>?) {
                    EventBus.getDefault().post(MeetEventBackPressedConference(p0))
                }

                @SuppressLint("LogNotTimber")
                override fun onCustomEvent(p0: MutableMap<String, Any>?) {
                    EventBus.getDefault().post(MeetEventCustom(p0))
                }

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
            MeetHolder.getJitsiView()?.leave()
            MeetHolder.removeJitsiView()
            val intent = Intent(this, CreateNotfication::class.java)
            this.stopService(intent)
        }
        val typeCall: QiscusMeet.Type = MeetSharePref.getType().let {
            when (it) {
                QiscusMeet.Type.CONFERENCE.toString() -> QiscusMeet.Type.CONFERENCE
                QiscusMeet.Type.VOICE.toString() -> QiscusMeet.Type.VOICE
                QiscusMeet.Type.VIDEO.toString() -> QiscusMeet.Type.VIDEO
                else -> QiscusMeet.Type.CONFERENCE
            }
        }
        EventBus.getDefault()
            .post(MeetTerminatedConferenceEvent(MeetSharePref.getRoomId(), null, typeCall))

        finish()
    }

    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {
        JitsiMeetActivityDelegate.requestPermissions(this, p0, p1, p2)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onNewIntent(intent: Intent?) {
        JitsiMeetActivityDelegate.onNewIntent(intent)
        super.onNewIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        JitsiMeetActivityDelegate.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data)
    }
}
