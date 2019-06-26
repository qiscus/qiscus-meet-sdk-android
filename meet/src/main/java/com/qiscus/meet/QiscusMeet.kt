package com.qiscus.meet

import android.app.Application
import org.greenrobot.eventbus.EventBus
import org.jitsi.meet.sdk.JitsiMeet
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import java.net.MalformedURLException
import java.net.URL

/**
 * Created on : 13/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class QiscusMeet {

    companion object {
        private lateinit var application: Application
        private lateinit var url: URL
        private lateinit var config: JitsiMeetConferenceOptions

        @JvmStatic
        fun init(application: Application, url: String) {
            this.application = application
            try {
                this.url = URL(url)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
                throw RuntimeException("Invalid server URL!")
            }

            config = JitsiMeetConferenceOptions.Builder()
                .setServerURL(this.url)
                .setWelcomePageEnabled(false)
                .build()

            JitsiMeet.setDefaultConferenceOptions(config)
        }

        @JvmStatic
        fun launch(): MeetInfo {
            if(!this::application.isInitialized) {
                throw RuntimeException("Please init QiscusMeet first")
            }
            return MeetInfo()
        }

        @JvmStatic
        fun event(event: QiscusMeetEvent) {
            EventBus.getDefault().post(MeetEvent(event))
        }
    }

    enum class CallType {
        VOICE, VIDEO
    }

    enum class CallAs {
        CALLEE, CALLER
    }

    enum class QiscusMeetEvent{
        REJECTED
    }

}