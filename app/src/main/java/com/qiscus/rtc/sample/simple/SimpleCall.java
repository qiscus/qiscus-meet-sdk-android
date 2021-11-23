package com.qiscus.rtc.sample.simple;

import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qiscus.meet.MeetConfig;
import com.qiscus.meet.MeetInfo;
import com.qiscus.meet.MeetJwtConfig;
import com.qiscus.meet.QiscusMeet;
import com.qiscus.rtc.sample.R;
import com.qiscus.rtc.sample.service.EndCall;

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
        //Setup Config
        QiscusMeet.config().setJwtConfig(meetJwtConfig);
        QiscusMeet.config().setChat(true);
        QiscusMeet.config().setOverflowMenu(true);
        QiscusMeet.config().setAutoRecording(true);
        QiscusMeet.config().setEnableBackPressed(true);
        QiscusMeet.config().setScreenSharing(true);
        QiscusMeet.config().setRecording(true);
        QiscusMeet.config().setRaiseHand(true);
        QiscusMeet.config().setReactions(true); //
        QiscusMeet.config().setSecurityOptions(true); //
        QiscusMeet.config().setToolbox(true);
        QiscusMeet.config().setToolboxAlwaysVisible(true);
        QiscusMeet.config().setTileView(true);
        QiscusMeet.config().setParticipantMenu(true);
        QiscusMeet.config().setVideoMuteButton(true);
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
        //Start Call
        QiscusMeet.call()
                .setTypeCall(QiscusMeet.Type.CONFERENCE)
                .setRoomId(roomId)
                .setDisplayName(name)
                .setMuted(false)
                .setAvatar(avatar)
                .build(this);
//        startService(new Intent(this, EndCall.class));
    }
}
