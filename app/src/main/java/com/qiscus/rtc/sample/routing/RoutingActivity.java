package com.qiscus.rtc.sample.routing;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qiscus.rtc.sample.R;
import com.qiscus.rtc.sample.utils.RouterConnection;

import org.json.JSONException;
import org.json.JSONObject;

public class RoutingActivity extends AppCompatActivity {
    private static final String TAG = RoutingActivity.class.getSimpleName();
    private RouterConnection httpConnection;
    private Button call;
    private int attempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing);

        attempt = 0;
        call = findViewById(R.id.btn_call_agent);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCall();
            }
        });
    }

    private void doCall() {
        String callerId = "UserId" + String.valueOf(System.currentTimeMillis()/1000);
        String callerName = "User " + callerId;

        JSONObject request = new JSONObject();
        try {
            request.put("callerId", callerId);
            request.put("callerName", callerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpConnection = new RouterConnection("POST", "/router/call", request.toString(), new RouterConnection.AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                Log.e(TAG, "API connection error: " + errorMessage);
                Toast.makeText(RoutingActivity.this, "API connection error: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onHttpComplete(String response) {
                Log.d(TAG, "API connection success: " + response);
                try {
                    JSONObject objStream = new JSONObject(response);
                    boolean success = objStream.getBoolean("success");

                    if (success) {
                        JSONObject data = objStream.getJSONObject("data");
                        String room = data.getString("room");
                        JSONObject caller = data.getJSONObject("agent");
                        String agentId = caller.getString("userId");
                        String agentName = caller.getString("userName");
                        String agentAvatar = caller.getString("userAvatar");
//                        QiscusRtc.buildCallWith(room)
//                                .setCallAs(QiscusRtc.CallAs.CALLER)
//                                .setCallType(QiscusRtc.CallType.VIDEO)
//                                .setCallerUsername(callerId)
//                                .setCalleeUsername(agentId)
//                                .setCalleeDisplayName(agentName)
//                                .setCalleeDisplayAvatar(agentAvatar)
//                                .show(RoutingActivity.this);
                    } else {
                        if (attempt < 3) {
                            attempt++;
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    doCall();
                                }
                            }, 2500);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        });

        httpConnection.setContentType("application/json");
        httpConnection.send();
    }
}
