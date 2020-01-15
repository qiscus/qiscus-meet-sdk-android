package com.qiscus.rtc.sample.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qiscus.meet.QiscusMeet;
import com.qiscus.rtc.sample.R;

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

        QiscusMeet.answer()
                .setTypeCall(QiscusMeet.Type.VOICE)
                .setRoomId(roomId)
                .setDisplayName(name)
                .setAvatar(avatar)
                .build(this);
    }
}
