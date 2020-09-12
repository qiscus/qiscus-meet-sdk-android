package com.qiscus.meet.app.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.qiscus.meet.app.R;
import com.qiscus.meet.app.utils.PreferencesHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {

    private Button btnStart;
    private EditText etRoomId;
    private EditText etName;
    private String username="";
    private TextView tvPrivacy, env;
    private ImageView imageView;
    PreferencesHelper preferencesHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        username = intent.getStringExtra("roomId");

        preferencesHelper = PreferencesHelper.getInstance(getApplicationContext());

        btnStart = findViewById(R.id.btn_start);
        etRoomId = findViewById(R.id.et_room);
        etName = findViewById(R.id.et_name);
        tvPrivacy = findViewById(R.id.tv_privacy);
        imageView = findViewById(R.id.logo_meet);
        env = findViewById(R.id.env);

        if (username != null){
            etRoomId.setText(username);
            btnStart.setText("Join Conference");
        }

        etName.setText(preferencesHelper.getName());

        if (preferencesHelper.getConfigType() == PreferencesHelper.Type.STAGING) {
            env.setText(preferencesHelper.getConfigType().toString());
        }

        btnStart.setOnClickListener(v -> {
            startCall();
        });

        imageView.setOnLongClickListener(v -> {

            showDialog();

            return true;
        });

        tvPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.qiscus.com/privacy-policy"));
                startActivity(intent);
            }
        });
    }

    private void showDialog() throws Resources.NotFoundException {
        PreferencesHelper.Type mode = PreferencesHelper.getInstance(getApplicationContext()).getConfigType();
        PreferencesHelper.Type otherMode = mode == PreferencesHelper.Type.PRODUCTION ? PreferencesHelper.Type.STAGING : PreferencesHelper.Type.PRODUCTION;

        new AlertDialog.Builder(this)
                .setTitle("Development Environment")
                .setMessage(String.format("Now you are in %s mode,"
                        + " do you want to switch mode to %s mode?"
                        + " after switch mode you must restart the apps to make apps working properly.", mode.name(), otherMode.name()))
                .setIcon(
                        getResources().getDrawable(
                                android.R.drawable.ic_dialog_alert))
                .setPositiveButton("Yes",
                        (dialog, which) -> {
                            //Do Something Here
                            preferencesHelper.setConfigType(otherMode);
                            Toast.makeText(getApplicationContext(), "Please kill the apps, and then open it again!!!", Toast.LENGTH_SHORT).show();
                        })
                .setNegativeButton("No",
                        (dialog, which) -> {
                            //Do Something Here
                        }).show();
    };

    private boolean checkRoomName(String roomName) {
        String regex = "^[a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(roomName);
        return matcher.matches();
    }

    private void startCall() {
        String roomId = etRoomId.getText().toString();
        String name = etName.getText().toString();

        if (name.length() == 0) {
            Toast.makeText(this, "Name required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (roomId.length() == 0) {
            Toast.makeText(this, "room id required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkRoomName(roomId)) {
            Toast.makeText(this, "Room name alphabet and numeric is allowed", Toast.LENGTH_SHORT).show();
            return;
        }

        preferencesHelper.setName(name);
        Intent intent = new Intent(HomeActivity.this, ChooseConferenceActivity.class);
        intent.putExtra("username", name);
        intent.putExtra("roomId", roomId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
}
