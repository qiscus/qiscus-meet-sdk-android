package com.qiscus.rtc.sample;

import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.qiscus.meet.MeetParticipantJoinedEvent;
import com.qiscus.meet.MeetParticipantLeftEvent;
import com.qiscus.meet.MeetTerminatedConfEvent;
import com.qiscus.meet.QiscusMeet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

public class SampleApplication extends MultiDexApplication {

    private static SampleApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        QiscusMeet.setup(this, "meetstage-iec22sd", "https://call.qiscus.com");
    }

    public static SampleApplication getInstance() {
        return instance;
    }



    private void handleCustomEvent(String roomId, String event) {
        if (event.equalsIgnoreCase("rejected")) {
            QiscusMeet.event(QiscusMeet.QiscusMeetEvent.REJECTED, roomId);
        }
    }

    @Subscribe
    public void onTerminatedConf(MeetTerminatedConfEvent event) {
        Log.d("ON TerminatedConf", event.getData().toString());

    }
    @Subscribe
    public void onParticipantLeft(MeetParticipantLeftEvent event){
        Log.d("ON PARTICIPANT LEFT", event.getData().toString());
        QiscusMeet.endCall();
    }

    @Subscribe
    public void onParticipantJoined(MeetParticipantJoinedEvent event){
        Log.d("ON PARTICIPANT JOINED", event.getData().toString());
    }
}
