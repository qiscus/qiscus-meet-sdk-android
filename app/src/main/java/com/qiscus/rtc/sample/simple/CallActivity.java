package com.qiscus.rtc.sample.simple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.qiscus.rtc.sample.R;

public class CallActivity extends AppCompatActivity {
    private static final String TAG = CallActivity.class.getSimpleName();

    private Button call;
    private RadioGroup call_as;
    private RadioButton callAs;
    private RadioGroup call_type;
    private RadioButton callType;
    private EditText target;
    private EditText roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_call);

        target = (EditText) findViewById(R.id.target);
        roomId = (EditText) findViewById(R.id.room_id);
        roomId.setText(generateRoomCall());

        call_as = (RadioGroup) findViewById(R.id.call_as);
        call_type = (RadioGroup) findViewById(R.id.call_type);

        call = (Button) findViewById(R.id.btn_call);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId;

                selectedId = call_as.getCheckedRadioButtonId();
                callAs = (RadioButton) findViewById(selectedId);

                selectedId = call_type.getCheckedRadioButtonId();
                callType = (RadioButton) findViewById(selectedId);

                if (!target.getText().toString().isEmpty() || !roomId.getText().toString().isEmpty()) {
                    if (callAs.getText().toString().equals("Caller")) {
//                        QiscusRtc.buildCallWith(roomId.getText().toString())
//                                .setCallAs(QiscusRtc.CallAs.CALLER)
//                                .setCallType(callType.getText().toString().equals("Voice") ? QiscusRtc.CallType.VOICE : QiscusRtc.CallType.VIDEO)
//                                .setCallerUsername(QiscusRtc.getAccount().getUsername())
//                                .setCalleeUsername(target.getText().toString())
//                                .setCalleeDisplayName(target.getText().toString())
//                                .setCalleeDisplayAvatar("http://dk6kcyuwrpkrj.cloudfront.net/wp-content/uploads/sites/45/2014/05/avatar-blank.jpg")
//                                .show(CallActivity.this);
                    } else {
//                        QiscusRtc.buildCallWith(roomId.getText().toString())
//                                .setCallAs(QiscusRtc.CallAs.CALLEE)
//                                .setCallType(callType.getText().toString().equals("Voice") ? QiscusRtc.CallType.VOICE : QiscusRtc.CallType.VIDEO)
//                                .setCalleeUsername(QiscusRtc.getAccount().getUsername())
//                                .setCallerUsername(target.getText().toString())
//                                .setCallerDisplayName(target.getText().toString())
//                                .setCallerDisplayAvatar("http://dk6kcyuwrpkrj.cloudfront.net/wp-content/uploads/sites/45/2014/05/avatar-blank.jpg")
//                                .show(CallActivity.this);
                    }
                } else {
                    Toast.makeText(CallActivity.this, "Target or room required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String generateRoomCall() {
        String room = "CallRoom_" + String.valueOf(System.currentTimeMillis());
        return room;
    }
}
