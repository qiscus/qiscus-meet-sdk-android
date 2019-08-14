package com.qiscus.rtc.sample;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.qiscus.meet.QiscusMeet;
import com.qiscus.rtc.sample.simple.IncomingCallActivity;
import com.qiscus.rtc.sample.utils.Config;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.chat.core.data.model.QiscusComment;
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

public class SampleApplication extends Application {

    private static SampleApplication instance;
    private AppComponent component;

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

        QiscusMeet.init(this,"https://meet.qiscus.com");
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
}
