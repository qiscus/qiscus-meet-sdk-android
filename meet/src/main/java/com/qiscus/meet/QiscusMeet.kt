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
        lateinit var application: Application
        private lateinit var url: URL
        private lateinit var config: JitsiMeetConferenceOptions
        private lateinit var qiscusConfig: MeetConfig

        @JvmStatic
        fun setup(application: Application, url: String) {
            this.qiscusConfig = MeetConfig()
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
        fun config(): MeetConfig {
            if (!this::application.isInitialized) {
                throw RuntimeException("Please init QiscusMeet first")
            }
            return qiscusConfig
        }

        @JvmStatic
        fun call(): MeetInfo {
            if (!this::application.isInitialized) {
                throw RuntimeException("Please init QiscusMeet first")
            }
            return MeetInfo(url.toString(), QiscusMeet.TypeCaller.CALLER, qiscusConfig)
        }

        @JvmStatic
        fun answer(): MeetInfo {
            if (!this::application.isInitialized) {
                throw RuntimeException("Please init QiscusMeet first")
            }
            return MeetInfo(url.toString(), QiscusMeet.TypeCaller.CALLEE, qiscusConfig)
        }

        @JvmStatic
        fun sendEvent() {

        }

        @JvmStatic
        fun event(event: QiscusMeetEvent, roomId: String) {
            EventBus.getDefault().post(MeetEvent(roomId, event))
        }
    }

    enum class Type {
        VOICE, VIDEO, CONFERENCE
    }

    enum class TypeCaller {
        CALLER, CALLEE
    }

    enum class QiscusMeetEvent {
        REJECTED
    }

}