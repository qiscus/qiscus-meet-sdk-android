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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_call2);

        btnStart = findViewById(R.id.btn_start);
        etRoomId = findViewById(R.id.et_room);

        btnStart.setOnClickListener(v-> {
            startCall();
        });
    }

    private void startCall() {
        String roomId = etRoomId.getText().toString();

        if(roomId.length() == 0) {
            Toast.makeText(this, "room id required", Toast.LENGTH_SHORT).show();
            return;
        }

        QiscusMeet.launch()
                .setRoomId(roomId)
                .build(this);
    }
}
