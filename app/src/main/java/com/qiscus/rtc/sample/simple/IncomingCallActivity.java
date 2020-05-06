package com.qiscus.rtc.sample.simple;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiscus.meet.QiscusMeet;
import com.qiscus.rtc.sample.R;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi;

import org.json.JSONObject;

public class IncomingCallActivity extends AppCompatActivity {

    private ImageView ivCallerAvatar;
    private TextView tvCallerName;
    private Button btnAcceptCall, btnDeniedCall;

    private String roomId, callerAvatar, callerDisplayName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        roomId = getIntent().getStringExtra("roomId");
        callerAvatar = getIntent().getStringExtra("callerAvatar");
        callerDisplayName = getIntent().getStringExtra("callerDisplayName");

        ivCallerAvatar = findViewById(R.id.iv_caller_avatar);
        tvCallerName = findViewById(R.id.tv_caller_name);
        btnAcceptCall = findViewById(R.id.btn_accept_call);
        btnDeniedCall = findViewById(R.id.btn_denied_call);

        tvCallerName.setText(callerDisplayName);
        Glide.with(this).load(callerAvatar).into(ivCallerAvatar);

        btnAcceptCall.setOnClickListener(v -> {
            onAccept();
        });

        btnDeniedCall.setOnClickListener(v -> {
            onDenied();
        });
    }

    private void onAccept() {
        QiscusMeet.call()
                .setRoomId(roomId)
                .setDisplayName(Qiscus.getQiscusAccount().getUsername())
                .build(this);

        finish();
    }

    private void onDenied() {
        JSONObject json = new JSONObject();
        try {
            json.put("sender", roomId);
            json.put("event", "rejected");
            json.put("active", false);

            QiscusPusherApi.getInstance().setEvent(Long.parseLong(roomId), json);

        } catch (Exception ex) {
            Log.e("IncomingCallActivity", ex.getMessage());
        }

        this.finish();

    }
}
