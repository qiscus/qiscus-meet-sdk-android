package com.qiscus.meet.app;

import android.content.Intent;
import androidx.multidex.MultiDexApplication;

import com.qiscus.meet.MeetTerminatedConfEvent;
import com.qiscus.meet.QiscusMeet;
import com.qiscus.meet.app.activity.HomeActivity;
import com.qiscus.meet.app.utils.PreferencesHelper;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class SampleApplication extends MultiDexApplication {

    private static SampleApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        QiscusMeet.setup(this, "https://meet.qiscus.com");

        QiscusMeet.config()
                .setPassword(true)
                .setChat(true)
                .setVideoThumbnailsOn(false)
                .setOverflowMenu(true);
    }

    public static SampleApplication getInstance() {
        return instance;
    }

    @Subscribe
    public void MeetTerminatedConfEvent(MeetTerminatedConfEvent event) {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
