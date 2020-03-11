package com.qiscus.meet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetView
import org.jitsi.meet.sdk.JitsiMeetViewListener

/**
 * Created on : 27/02/20
 * Author     : arioki
 * Name       : Yoga Setiawan
 * GitHub     : https://github.com/arioki
 */

class MeetHolder : JitsiHolderInterface {
    companion object {
        private var jitsiMeetView: JitsiMeetView? = null
        fun getJitsiView(): JitsiMeetView? {
            return jitsiMeetView
        }

        fun removeJitsiView() {
            jitsiMeetView = null
        }
    }

    private var context: Context? = null
    override fun initialise(context: Context) {
        this.context = context
        if (jitsiMeetView == null) {
            jitsiMeetView = JitsiMeetView(context)
            getJitsiView()?.listener = object : JitsiMeetViewListener {
                @SuppressLint("LogNotTimber")
                override fun onConferenceTerminated(p0: MutableMap<String, Any>?) {
                    stopCall()
                }

                @SuppressLint("LogNotTimber")
                override fun onConferenceJoined(p0: MutableMap<String, Any>?) {
                    createNotification(context)
                }

                @SuppressLint("LogNotTimber")
                override fun onConferenceWillJoin(p0: MutableMap<String, Any>?) {
                    Log.d("LOG_CONF_WillJoin", p0.toString())
                }
            }
        }
    }

    fun createNotification(context: Context) {
        getJitsiView()?.let {
            val intent = Intent(context, CreateNotfication::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    override fun requestCallStart(room: String, options: JitsiMeetConferenceOptions) {
        doCallStart(room, options)
    }

    override fun stopCall() {
        getJitsiView()?.leave()
        val intent = Intent(context, CreateNotfication::class.java)
        context?.stopService(intent)
        removeJitsiView()
    }

    private fun doCallStart(room: String, options: JitsiMeetConferenceOptions) {
        if (jitsiMeetView == null) return
        getJitsiView()?.join(options)
        context?.let {
            createNotification(it)
        }
    }
}

interface JitsiHolderInterface {
    fun initialise(context: Context)
    fun requestCallStart(room: String, options: JitsiMeetConferenceOptions)
    fun stopCall()
}