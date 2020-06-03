package com.qiscus.rtc.sample;

import android.content.Intent;
import android.os.Handler;
import androidx.multidex.MultiDexApplication;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.qiscus.meet.MeetTerminatedConfEvent;
import com.qiscus.meet.QiscusMeet;
import com.qiscus.rtc.sample.integration.ChatActivity;
import com.qiscus.rtc.sample.simple.IncomingCallActivity;
import com.qiscus.rtc.sample.utils.AsyncHttpUrlConnection;
import com.qiscus.rtc.sample.utils.Config;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.chat.core.data.model.QiscusComment;
import com.qiscus.sdk.chat.core.data.model.QiscusRoomMember;
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi;
import com.qiscus.sdk.chat.core.event.QiscusChatRoomEvent;
import com.qiscus.sdk.chat.core.event.QiscusCommentReceivedEvent;
import com.qiscus.sdk.data.model.QiscusNotificationBuilderInterceptor;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by fitra on 04/10/17.
 */

public class SampleApplication extends MultiDexApplication {

    private static SampleApplication instance;
    private AppComponent component;
    private AsyncHttpUrlConnection httpConnection;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        component = new AppComponent(this);
        Qiscus.init(this, Config.CHAT_APP_ID);
        Qiscus.getChatConfig().setNotificationBuilderInterceptor(new QiscusNotificationBuilderInterceptor() {
            @Override
            public boolean intercept(NotificationCompat.Builder notificationBuilder, QiscusComment qiscusComment) {
                if (qiscusComment.getType() == QiscusComment.Type.SYSTEM_EVENT) {
                    return false;
                }
                return true;
            }
        });

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        QiscusMeet.setup(this, "https://dgcall.qiscus.com");
    }

    public static SampleApplication getInstance() {
        return instance;
    }

    public AppComponent getComponent() {
        return component;
    }

    @Subscribe
    public void onReceivedComment(QiscusCommentReceivedEvent event) {
        if (event.getQiscusComment().getExtraPayload() != null && !event.getQiscusComment().getExtraPayload().equals("null")) {
            handleCallPn(event.getQiscusComment());
        }
    }

    @Subscribe
    public void onReceiveRoomEvent(QiscusChatRoomEvent roomEvent) {
        switch (roomEvent.getEvent()) {
            case CUSTOM:
                //here, you can listen custom event
                roomEvent.getRoomId(); // this is the room id
                roomEvent.getUser(); // this is the sender's qiscus user id
                JSONObject json = roomEvent.getEventData();//event data (JSON)
                try {
                    String event = json.getString("event");
                    String roomId = json.getString("sender");
                    handleCustomEvent(roomId, event);
                } catch (Exception ex) {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    private void handleCustomEvent(String roomId, String event) {
        if (event.equalsIgnoreCase("rejected")) {
            QiscusMeet.event(QiscusMeet.QiscusMeetEvent.REJECTED, roomId);
        }
    }

    private void handleCallPn(QiscusComment remoteMessage) {
        JSONObject json;
        try {
            json = new JSONObject(remoteMessage.getExtraPayload());
            JSONObject payload = json.getJSONObject("payload");

            if (payload.get("type").equals("call") || payload.get("type").equals("webview_call")) {
                String event = payload.getString("call_event");
                switch (event.toLowerCase()) {
                    case "incoming":
                        final String roomId = payload.get("call_room_id").toString();

                        JSONObject caller = payload.getJSONObject("call_caller");
                        final String caller_email = caller.getString("username");
                        final String caller_name = caller.getString("name");
                        final String caller_avatar = caller.getString("avatar");
                        JSONObject callee = payload.getJSONObject("call_callee");
                        final String callee_email = callee.getString("username");
                        final String callee_name = callee.getString("name");
                        final String callee_avatar = callee.getString("avatar");

                        if (Qiscus.getQiscusAccount().getEmail().equals(callee_email)) {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {

                                Intent intent = new Intent(getApplicationContext(), IncomingCallActivity.class);
                                intent.putExtra("callerAvatar", caller_avatar);
                                intent.putExtra("callerDisplayName", caller_name);
                                intent.putExtra("roomId", roomId);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }, 2500);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onTerminatedConf(MeetTerminatedConfEvent event) {
        Log.e("debug event", event.getRoomId());
        endCall(event.getRoomId());
        JSONObject json = new JSONObject();
        try {
            json.put("sender", event.getRoomId());
            json.put("event", "rejected");
            json.put("active", false);

            QiscusPusherApi.getInstance().setEvent(Long.parseLong(event.getRoomId()), json);

        } catch (Exception ex) {
            Log.e("IncomingCallActivity", ex.getMessage());
        }
    }

    private void endCall(String roomId) {
        JSONObject request = new JSONObject();
        JSONObject payload = new JSONObject();
        JSONObject caller = new JSONObject();
        JSONObject callee = new JSONObject();

        try {
            request.put("system_event_type", "custom");
            request.put("room_id", roomId);
            request.put("message", Qiscus.getQiscusAccount().getUsername() + " endcall ");
            payload.put("type", "endcall");
            payload.put("call_event", "endcall");
            payload.put("call_room_id", roomId);
            payload.put("call_is_video", true);
            caller.put("username", Qiscus.getQiscusAccount().getEmail());
            caller.put("name", Qiscus.getQiscusAccount().getUsername());
            caller.put("avatar", Qiscus.getQiscusAccount().getAvatar());
            payload.put("call_caller", caller);
            payload.put("call_callee", callee);
            request.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpConnection = new AsyncHttpUrlConnection("POST", "/api/v2/rest/post_system_event_message", request.toString(), new AsyncHttpUrlConnection.AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                Log.e("TAG", "API connection error: " + errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
                Log.d("TAG", "API connection success: " + response);
            }
        });
        httpConnection.setContentType("application/json");
        httpConnection.send();
    }
}
