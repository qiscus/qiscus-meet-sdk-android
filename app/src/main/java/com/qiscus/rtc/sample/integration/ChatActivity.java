package com.qiscus.rtc.sample.integration;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiscus.meet.QiscusMeet;
import com.qiscus.rtc.sample.R;
import com.qiscus.rtc.sample.utils.AsyncHttpUrlConnection;
import com.qiscus.rtc.sample.utils.Config;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.chat.core.data.model.QiscusRoomMember;
import com.qiscus.sdk.chat.core.data.remote.QiscusPusherApi;
import com.qiscus.sdk.ui.QiscusBaseChatActivity;
import com.qiscus.sdk.ui.fragment.QiscusBaseChatFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class ChatActivity extends QiscusBaseChatActivity {
    private static final String TAG = ChatActivity.class.getSimpleName();

    private AsyncHttpUrlConnection httpConnection;
    private TextView title;
    private ImageView back;
    private ImageView voiceCall;
    private ImageView videoCall;

    public static Intent generateIntent(Context context, QiscusChatRoom qiscusChatRoom) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(CHAT_ROOM_DATA, qiscusChatRoom);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_chat;
    }

    @Override
    protected void onLoadView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView) findViewById(R.id.tv_title);
        back = (ImageView) findViewById(R.id.back);
        voiceCall = (ImageView) findViewById(R.id.voice_call);
        videoCall = (ImageView) findViewById(R.id.video_call);
    }

    @Override
    protected QiscusBaseChatFragment onCreateChatFragment() {
        return ChatFragment.newInstance(qiscusChatRoom);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);

        QiscusPusherApi.getInstance().listenEvent(qiscusChatRoom.getId());

        title.setText(qiscusChatRoom.getName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        voiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (QiscusRoomMember member : qiscusChatRoom.getMember()) {
                    if (!member.getEmail().equalsIgnoreCase(Qiscus.getQiscusAccount().getEmail())) {
                        startVoiceCall(member);
                    }
                }
            }
        });
        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (QiscusRoomMember member : qiscusChatRoom.getMember()) {
                    if (!member.getEmail().equalsIgnoreCase(Qiscus.getQiscusAccount().getEmail())) {
                        startVideoCall(member);
                    }
                }
            }
        });
    }

    @Override
    public void onUserStatusChanged(String user, boolean online, Date lastActive) {
        //
    }

    @Override
    public void onUserTyping(String user, boolean typing) {
        //
    }

    private void startVoiceCall(final QiscusRoomMember target) {
        JSONObject request = new JSONObject();
        JSONObject payload = new JSONObject();
        JSONObject caller = new JSONObject();
        JSONObject callee = new JSONObject();
        final String roomId = String.valueOf(qiscusChatRoom.getId());
        try {
            request.put("system_event_type", "custom");
            request.put("room_id",String.valueOf(qiscusChatRoom.getId()));
            request.put("subject_email", target.getEmail());
            request.put("message", Qiscus.getQiscusAccount().getUsername() + " call " + target.getUsername());
            payload.put("type", "call");
            payload.put("call_event", "incoming");
            payload.put("call_room_id", roomId);
            payload.put("call_is_video", false);
            caller.put("username", Qiscus.getQiscusAccount().getEmail());
            caller.put("name", Qiscus.getQiscusAccount().getUsername());
            caller.put("avatar", Qiscus.getQiscusAccount().getAvatar());
            callee.put("username", target.getEmail());
            callee.put("name", target.getUsername());
            callee.put("avatar", target.getAvatar());
            payload.put("call_caller", caller);
            payload.put("call_callee", callee);
            request.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpConnection = new AsyncHttpUrlConnection("POST", "/api/v2/rest/post_system_event_message", request.toString(), new AsyncHttpUrlConnection.AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                Log.e(TAG, "API connection error: " + errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
                Log.d(TAG, "API connection success: " + response);
                try {
                    JSONObject objStream = new JSONObject(response);
                    if (objStream.getInt("status") == 200) {

                        QiscusMeet.launch()
                                .setRoomId(roomId)
                                .build(ChatActivity.this);
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

    private void startVideoCall(final QiscusRoomMember target) {
        JSONObject request = new JSONObject();
        JSONObject payload = new JSONObject();
        JSONObject caller = new JSONObject();
        JSONObject callee = new JSONObject();
        final String roomId = Config.CALL_APP_ID + "_" + String.valueOf(System.currentTimeMillis());
        try {
            request.put("system_event_type", "custom");
            request.put("room_id",String.valueOf(qiscusChatRoom.getId()));
            request.put("subject_email", target.getEmail());
            request.put("message", Qiscus.getQiscusAccount().getUsername() + " call " + target.getUsername());
            payload.put("type", "call");
            payload.put("call_event", "incoming");
            payload.put("call_room_id", roomId);
            payload.put("call_is_video", true);
            caller.put("username", Qiscus.getQiscusAccount().getEmail());
            caller.put("name", Qiscus.getQiscusAccount().getUsername());
            caller.put("avatar", Qiscus.getQiscusAccount().getAvatar());
            callee.put("username", target.getEmail());
            callee.put("name", target.getUsername());
            callee.put("avatar", target.getAvatar());
            payload.put("call_caller", caller);
            payload.put("call_callee", callee);
            request.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpConnection = new AsyncHttpUrlConnection("POST", "/api/v2/rest/post_system_event_message", request.toString(), new AsyncHttpUrlConnection.AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                Log.e(TAG, "API connection error: " + errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
                Log.d(TAG, "API connection success: " + response);
                try {
                    JSONObject objStream = new JSONObject(response);
                    if (objStream.getInt("status") == 200) {
//                        QiscusRtc.buildCallWith(roomId)
//                                .setCallAs(QiscusRtc.CallAs.CALLER)
//                                .setCallType(QiscusRtc.CallType.VIDEO)
//                                .setCallerUsername(Qiscus.getQiscusAccount().getEmail())
//                                .setCalleeUsername(target.getEmail())
//                                .setCalleeDisplayName(target.getUsername())
//                                .setCalleeDisplayAvatar(target.getAvatar())
//                                .show(ChatActivity.this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QiscusPusherApi.getInstance().unlistenEvent(qiscusChatRoom.getId());
    }
}
