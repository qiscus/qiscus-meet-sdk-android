package com.qiscus.meet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.qiscus.meet.model.Call
import kotlinx.android.synthetic.main.activity_incoming_call.*
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

class IncomingCallActivity : AppCompatActivity() {

    private lateinit var call: Call

    companion object {
        fun generateIntent(ctx: Context, call: Call): Intent {
            val intent = Intent(ctx, IncomingCallActivity::class.java)
            intent.putExtra("call", call)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)

        call = intent.getParcelableExtra("call")
        tv_caller_name.text = call.callerDisplayName
        Log.e("debug", call.callerAvatar)
        Glide.with(this).load(call.callerAvatar).into(iv_caller_avatar)
        btn_denied_call.setOnClickListener { onDenied() }
        btn_accept_call.setOnClickListener { onAccept() }
    }

    private fun onAccept() {
        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(call.roomId)
            .build()

        JitsiMeetActivity.launch(this, options)
        finish()
    }

    private fun onDenied() {
        this.finish()
    }
}
