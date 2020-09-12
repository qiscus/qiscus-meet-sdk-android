package com.qiscus.meet.app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.qiscus.meet.QiscusMeet;
import com.qiscus.meet.app.R;
import com.qiscus.meet.app.utils.PreferencesHelper;

public class ChooseConferenceActivity extends AppCompatActivity {

    private LinearLayout boxVideoCall, boxVoiceCall;
    private String username, roomId;
    private TextView etRoomId;
    private Switch aSwitch;
    ProgressDialog progressDoalog;
    String roomName = "";
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_conference);

        preferencesHelper = PreferencesHelper.getInstance(getApplicationContext());
        progressDoalog = new ProgressDialog(ChooseConferenceActivity.this);

        boxVideoCall = findViewById(R.id.box_video_call);
        boxVoiceCall = findViewById(R.id.box_voice_call);
        etRoomId = findViewById(R.id.et_room_id);
        aSwitch = findViewById(R.id.switch_muted);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        roomId = intent.getStringExtra("roomId");

        actionMuted(preferencesHelper.getMuted());

        Uri data = intent.getData();
        String roomDeepLink = "";
        if (data != null) {
            roomDeepLink = data.getPath().replace("/","");
        }

        roomName = roomDeepLink.isEmpty() ? roomId : roomDeepLink;
        etRoomId.setText("Room Name : " + roomName);

        if (preferencesHelper.getName().isEmpty()) {
            intent = new Intent(ChooseConferenceActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("roomId", roomName);
            startActivity(intent);
        }

        boxVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMeet(true);
            }
        });

        boxVoiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMeet(false);
            }
        });

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                actionMuted(isChecked);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDoalog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressDoalog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDoalog.dismiss();
    }

    private void actionMuted(boolean isChecked) {
        aSwitch.setText((isChecked)? "Mic muted" : "Mic Unmuted");
        aSwitch.setChecked(isChecked);
        preferencesHelper.setMuted(isChecked);
    }

    private void startMeet(boolean isVideo){
        progressDoalog.setMessage("Loading...");
        progressDoalog.setCancelable(false);
        progressDoalog.show();
        QiscusMeet.call()
                .setTypeCall(isVideo?QiscusMeet.Type.VIDEO:QiscusMeet.Type.VOICE)
                .setRoomId(roomName)
                .setMuted(preferencesHelper.getMuted())
                .setDisplayName(preferencesHelper.getName())
                .build(this);
    }
}
