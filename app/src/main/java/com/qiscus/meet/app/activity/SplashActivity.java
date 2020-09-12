package com.qiscus.meet.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.qiscus.meet.app.BuildConfig;
import com.qiscus.meet.app.R;

public class SplashActivity extends AppCompatActivity {

    private TextView etVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String versionName = BuildConfig.VERSION_NAME;

        etVersion = findViewById(R.id.et_version);
        etVersion.setText(versionName);

        int splashInterval = 3000;
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }, splashInterval);
    }
}
