package com.qiscus.rtc.sample.integration;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.qiscus.rtc.sample.MainActivity;
import com.qiscus.rtc.sample.R;
import com.qiscus.rtc.sample.SampleApplication;
import com.qiscus.rtc.sample.adapter.ChatRoomAdapter;
import com.qiscus.rtc.sample.adapter.OnItemClickListener;
import com.qiscus.rtc.sample.presenter.HomePresenter;
import com.qiscus.sdk.chat.core.data.model.QiscusChatRoom;

import java.util.List;

public class ContactActivity extends AppCompatActivity implements HomePresenter.View, OnItemClickListener {
    private static final String TAG = ContactActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private ChatRoomAdapter chatRoomAdapter;
    private HomePresenter homePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        chatRoomAdapter = new ChatRoomAdapter(this);
        chatRoomAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(chatRoomAdapter);

        homePresenter = new HomePresenter(this,
                SampleApplication.getInstance().getComponent().getChatRoomRepository(),
                SampleApplication.getInstance().getComponent().getUserRepository());

    }

    @Override
    protected void onResume() {
        super.onResume();
        homePresenter.loadChatRooms();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_chat) {
            homePresenter.createChatRoom();
        } else if (item.getItemId() == R.id.logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure wants to logout?")
                    .setPositiveButton("Logout", (dialog, which) -> homePresenter.logout())
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {
        homePresenter.openChatRoom(chatRoomAdapter.getData().get(position));
    }

    @Override
    public void showChatRooms(List<QiscusChatRoom> chatRooms) {
        chatRoomAdapter.addOrUpdate(chatRooms);
    }

    @Override
    public void showChatRoomPage(QiscusChatRoom chatRoom) {
        startActivity(ChatActivity.generateIntent(this, chatRoom));
    }

    @Override
    public void showContactPage() {
        startActivity(new Intent(this, ListContactActivity.class));
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMainPage() {
        startActivity(new Intent(this, MainActivity.class));
    }
}
