package com.qiscus.meet

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetView

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
        }
    }

    fun createNotification(context: Context) {
        getJitsiView()?.let {
            val intent = Intent(context, CreateNotfication::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    override fun requestCallStart(options: JitsiMeetConferenceOptions) {
        doCallStart(options)
    }


    private fun doCallStart(options: JitsiMeetConferenceOptions) {
        if (jitsiMeetView == null) return
        getJitsiView()?.join(options)
        context?.let {
            createNotification(it)
        }
    }
}

interface JitsiHolderInterface {
    fun initialise(context: Context)
    fun requestCallStart(options: JitsiMeetConferenceOptions)
}