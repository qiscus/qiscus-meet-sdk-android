package com.qiscus.rtc.sample.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.qiscus.meet.QiscusMeet;

public class EndCall extends Service {
    Handler handler = new Handler();
    int counter = 50;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (counter > 0) {

                Toast.makeText(getApplicationContext(), String.valueOf(counter), Toast.LENGTH_SHORT).show();
                Log.d("DURASI", String.valueOf(counter));
                counter = counter - 1;
                handler.postDelayed(runnable, 1000);
            } else {
                QiscusMeet.endCall();
                handler.removeCallbacks(runnable);
                getApplicationContext().stopService(new Intent(getApplicationContext(), EndCall.class));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        handler.postDelayed(runnable,1000);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
