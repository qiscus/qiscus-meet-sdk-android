package com.qiscus.rtc.sample.integration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.qiscus.rtc.sample.R;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;
import com.qiscus.sdk.ui.fragment.QiscusChatFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends QiscusChatFragment {
    public static ChatFragment newInstance(QiscusChatRoom qiscusChatRoom) {
        ChatFragment fragment = new ChatFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(CHAT_ROOM_DATA, qiscusChatRoom);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void onLoadView(View view) {
        super.onLoadView(view);
    }
}
