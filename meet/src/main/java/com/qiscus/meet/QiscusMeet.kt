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
        private var url: URL = URL("https://call.qiscus.com")
        private var appId: String = ""
        private lateinit var config: JitsiMeetConferenceOptions
        private lateinit var qiscusConfig: MeetConfig

        @JvmStatic
        fun setup(application: Application, appId: String, url: String) {
            this.qiscusConfig = MeetConfig()
            this.application = application
            this.appId = appId

            if (appId.isEmpty()) {
                throw RuntimeException("Please init Qiscus appId first")
            }

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
            if (!this::application.isInitialized && !hasSetupAppId()) {
                throw RuntimeException("Please init QiscusMeet first")
            }
            return MeetInfo(url.toString(), QiscusMeet.TypeCaller.CALLER, qiscusConfig)
        }

        @JvmStatic
        fun endCall() {
            QiscusMeetActivity.endCall()
        }

        @JvmStatic
        fun answer(): MeetInfo {
            if (!this::application.isInitialized && !hasSetupAppId()) {
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

        /**
         * For checking is Qiscus Meet appId has been setup
         *
         * @return true if already setup, false if not yet
         */
        @JvmStatic
        fun hasSetupAppId(): Boolean {
            return appId.isEmpty()
        }

        /**
         * Getting Qiscus Meet appId
         *
         * @return string
         */
        @JvmStatic
        fun getAppID(): String {
            return this.appId
        }

        /**
         * Getting URL Qiscus Meet
         *
         * @return URL
         */
        @JvmStatic
        fun getURL(): URL {
            return this.url
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