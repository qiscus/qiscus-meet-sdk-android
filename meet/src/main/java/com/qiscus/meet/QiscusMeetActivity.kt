package com.qiscus.meet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetFragment
import org.jitsi.meet.sdk.JitsiMeetView
import java.util.HashMap


/**
 * Created on : 21/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class QiscusMeetActivity : JitsiMeetActivity() {

    val TAG = this.javaClass.simpleName
    lateinit var roomId: String
    var enableBackpressed:Boolean = true
    var recordingEvent: RecordingEvent = RecordingEvent()
    companion object {
        val room = "roomid"
        val backpressed = "backpressed"
        val recording = "recording"
        const val ACTION_JITSI_MEET_CONFERENCE = "org.jitsi.meet.CONFERENCE"
        const val JITSI_MEET_CONFERENCE_OPTIONS = "JitsiMeetConferenceOptions"
        var activity: QiscusMeetActivity? = null
        fun launch(
            context: Context,
            options: Options?,
            roomid: String,
            enableBackpressed: Boolean
        ) {
            val intent = Intent(context, QiscusMeetActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.action = ACTION_JITSI_MEET_CONFERENCE
            intent.putExtra(JITSI_MEET_CONFERENCE_OPTIONS, options)
            intent.putExtra(room, roomid)
            intent.putExtra(backpressed, enableBackpressed)
            context.startActivity(intent)
        }
        fun launchWithRecording(
            context: Context,
            options: Options?,
            roomid: String,
            enableBackpressed: Boolean,
            recordingEvent: RecordingEvent
        ) {
            val intent = Intent(context, QiscusMeetActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.action = ACTION_JITSI_MEET_CONFERENCE
            intent.putExtra(JITSI_MEET_CONFERENCE_OPTIONS, options)
            intent.putExtra(room, roomid)
            intent.putExtra(backpressed, enableBackpressed)
            intent.putExtra(recording, recordingEvent)
            intent.putExtra("isRecording",true)
            context.startActivity(intent)
        }
        fun endCall() {
                MeetHolder.getJitsiView()?.leave()
                MeetHolder.removeJitsiView()
            val typeCall: QiscusMeet.Type = MeetSharePref.getType().let {
                when (it) {
                    QiscusMeet.Type.CONFERENCE.toString() -> QiscusMeet.Type.CONFERENCE
                    QiscusMeet.Type.VOICE.toString() -> QiscusMeet.Type.VOICE
                    QiscusMeet.Type.VIDEO.toString() -> QiscusMeet.Type.VIDEO
                    else -> QiscusMeet.Type.CONFERENCE
                }
            }
            EventBus.getDefault()
                .post(
                    MeetTerminatedConfEvent(
                        MeetSharePref.getRoomId(),
                        null,
                        typeCall
                    )
                )
            activity?.finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        roomId = intent.getStringExtra(room)!!
        enableBackpressed = intent.getBooleanExtra(backpressed, true)
        if (intent.getBooleanExtra("isRecording",false)!!){
            recordingEvent = intent.getParcelableExtra(recording)!!
            EventBus.getDefault().post(recordingEvent)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    protected fun getQiscusView(): JitsiMeetView? {
        val fragment =
            this.supportFragmentManager.findFragmentById(R.id.jitsiFragment) as JitsiMeetFragment?
        return fragment?.jitsiView
    }

    @Subscribe
    fun onReceivedEvent(meetEvent: MeetEvent) {
        if (meetEvent.event == QiscusMeet.QiscusMeetEvent.REJECTED && meetEvent.roomId == roomId) {
            jitsiView?.leave()
        }
    }

    override fun initialize() {
        val option = intent.getParcelableExtra<Options>(JITSI_MEET_CONFERENCE_OPTIONS)
        val options = JitsiMeetConferenceOptions.Builder()
            .setRoom(option?.roomURL)
            .setAudioMuted(option?.audioMuted!!)
            .setAudioOnly(option.audioOnly)
            .setFeatureFlag("pip.enabled", option.pipEnabled)
            .setFeatureFlag("requirepassword.enabled",option.requiredPasswordEnabled)
            .setFeatureFlag("chat.enabled", option.chatEnabled)
            .setFeatureFlag("overflow-menu.enabled", option.overFlowMenuEnabled)
            .setFeatureFlag("videoThumbnail.enabled", option.videoThumbnailEnabled)
            .setFeatureFlag("meeting-name.enabled", option.meetingNameEnabled)
            .setFeatureFlag("android.screensharing.enabled", option.androidScreenSharingEnabled)
            .setFeatureFlag("recording.enabled", option.recordingEnabled)
            .setFeatureFlag("reactions.enabled", option.reactionsEnabled)
            .setFeatureFlag("raise-hand.enabled", option.raiseHandEnabled)
            .setFeatureFlag("security-options.enabled", option.securityOptionsEnabled)
            .setFeatureFlag("toolbox.enabled", option.toolboxEnabled)
            .setFeatureFlag("toolbox.alwaysVisible", option.toolboxAlwaysVisible)
            .setFeatureFlag("tile-view.enabled", option.tileViewEnabled)
            .setFeatureFlag("participantMenu.enabled", option.participantMenuEnabled)
            .setFeatureFlag("videoMuteButton.enabled", option.videoMuteButtonEnabled)
            .setFeatureFlag("audioMuteButton.enabled", option.audioMuteButtonEnabled)
            .setToken(option.token)
            .setVideoMuted(option.videoMuted)
            .setFeatureFlag("autoRecording.enabled", option.autoRecordingEnabled)

        this.join(options.build())
    }

    override fun onConferenceTerminated(extraData: HashMap<String, Any>?) {
        super.onConferenceTerminated(extraData)
                EventBus.getDefault().post(MeetTerminatedConfEvent(roomId, extraData, null))
//        val intent = Intent(this, CreateNotfication::class.java)
//        this.stopService(intent)
    }

    override fun onConferenceJoined(extraData: HashMap<String, Any>?) {
        super.onConferenceJoined(extraData)
    }

    override fun onParticipantJoined(extraData: HashMap<String, Any>?) {
        super.onParticipantJoined(extraData)
        EventBus.getDefault().post(MeetParticipantJoinedEvent(roomId,extraData,null))
    }

    override fun onParticipantLeft(extraData: HashMap<String, Any>?) {
        super.onParticipantLeft(extraData)
        EventBus.getDefault().post(MeetParticipantLeftEvent(roomId,extraData,null))
    }
//    override fun onConferenceTerminated(data: MutableMap<String, Any>?) {
//        super.onConferenceTerminated(data)
//        EventBus.getDefault().post(MeetTerminatedConfEvent(roomId, data, null))
////        val intent = Intent(this, CreateNotfication::class.java)
//        this.stopService(intent)
//    }
//
//    override fun onConferenceJoined(data: MutableMap<String, Any>?) {
//        super.onConferenceJoined(data)
//
////        val intent = Intent(this, CreateNotfication::class.java)
//        ContextCompat.startForegroundService(this, intent)
//    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.cancel(0)
    }

    override fun onBackPressed() {
        if (enableBackpressed){
            super.onBackPressed()
        }
    }
}