package com.qiscus.meet

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread
import com.facebook.react.modules.core.PermissionListener
import org.greenrobot.eventbus.EventBus
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
        private val RECORD_REQUEST_CODE = 101
        var activity:Activity?=null
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
        activity = this
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
        finish()
    }

    override fun requestPermissions(p0: Array<out String>?, p1: Int, p2: PermissionListener?) {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("TAG", "Permission has been denied by user")
                } else {
                    Log.i("TAG", "Permission has been granted by user")
                }
            }
        }
    }
}
