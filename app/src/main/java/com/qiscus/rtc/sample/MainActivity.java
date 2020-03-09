package com.qiscus.rtc.sample;

import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.qiscus.rtc.sample.integration.ContactActivity;
import com.qiscus.rtc.sample.presenter.LoginPresenter;
import com.qiscus.rtc.sample.simple.SimpleCall;
import com.qiscus.sdk.Qiscus;

public class MainActivity extends AppCompatActivity implements LoginPresenter.View {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button simple;
    private Button integration;
    private Button logout;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        simple = findViewById(R.id.btn_simple);
        integration = findViewById(R.id.btn_chat_integration);
        logout = findViewById(R.id.btn_logout);

        final LoginPresenter loginPresenter = new LoginPresenter(this,
                SampleApplication.getInstance().getComponent().getUserRepository());
        loginPresenter.start();

        logout.setOnClickListener(view -> {
            logout.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Success Logout", Toast.LENGTH_SHORT).show();
            Qiscus.clearUser();
        });

        simple.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SimpleCall.class);
            startActivity(intent);
        });

        integration.setOnClickListener(v -> {
            if (Qiscus.hasSetupUser()) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class));
            } else {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View dialog = inflater.inflate(R.layout.dialog_login, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(dialog);
                alertDialogBuilder.setCancelable(false);

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                dialog.findViewById(R.id.login_user1).setOnClickListener(view -> loginPresenter.login(
                        "User 1 Sample Call",
                        "user1_sample_call@example.com",
                        "123"
                ));
                dialog.findViewById(R.id.login_user2).setOnClickListener(view -> loginPresenter.login(
                        "User 2 Sample Call",
                        "user2_sample_call@example.com",
                        "123"
                ));
                dialog.findViewById(R.id.login_user4).setOnClickListener(view -> loginPresenter.login(
                        "User 4 Sample Call",
                        "user4_sample_call@example.com",
                        "123"
                ));
                dialog.findViewById(R.id.login_user5).setOnClickListener(view -> loginPresenter.login(
                        "User 5 Sample Call",
                        "user5_sample_call@example.com",
                        "123"
                ));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResumeChat: " + Qiscus.hasSetupUser());
        if (Qiscus.hasSetupUser()) {
            logout.setVisibility(View.VISIBLE);
            logout.setText("Logout as " + Qiscus.getQiscusAccount().getUsername());
        } else {
            logout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void showHomePage() {

    }

    @Override
    public void successLogin() {
        startActivity(new Intent(MainActivity.this, ContactActivity.class));
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {
        alertDialog.dismiss();
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
