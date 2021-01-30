package com.qiscus.rtc.sample.simple;

import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qiscus.meet.MeetJwtConfig;
import com.qiscus.meet.QiscusMeet;
import com.qiscus.rtc.sample.R;
import com.qiscus.rtc.sample.service.EndCall;
import com.qiscus.sdk.Qiscus;

public class SimpleCall extends AppCompatActivity {

    private Button btnStart;
    private EditText etRoomId;
    private EditText etName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_call2);

        btnStart = findViewById(R.id.btn_start);
        etRoomId = findViewById(R.id.et_room);
        etName = findViewById(R.id.et_name);
        MeetJwtConfig meetJwtConfig = new MeetJwtConfig();
        meetJwtConfig.setEmail("marco@qiscus.com");
        meetJwtConfig.build();
        QiscusMeet.config().setJwtConfig(meetJwtConfig);
        btnStart.setOnClickListener(v -> {
            startCall();
        });
    }

    private void startCall() {
        String roomId = etRoomId.getText().toString();
        String name = etName.getText().toString();
        //hardcoded avatar
        String avatar = "https://dw9to29mmj727.cloudfront.net/misc/newsletter-naruto3.png";

        if (name.length() == 0) {
            Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (roomId.length() == 0) {
            Toast.makeText(this, "room id required", Toast.LENGTH_SHORT).show();
            return;
        }

        QiscusMeet.call()
                .setTypeCall(QiscusMeet.Type.VOICE)
                .setRoomId(roomId)
                .setDisplayName(name)
                .setAvatar(avatar)
                .build(this);
//        startService(new Intent(this, EndCall.class));
    }
}
