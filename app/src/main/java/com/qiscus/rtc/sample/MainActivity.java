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

//import com.qiscus.rtc.sample.integration.ContactActivity;
//import com.qiscus.rtc.sample.presenter.LoginPresenter;
import com.qiscus.rtc.sample.model.VersionResponse;
import com.qiscus.rtc.sample.retrofit.EndPoint;
import com.qiscus.rtc.sample.retrofit.RetrofitClient;
import com.qiscus.rtc.sample.simple.SimpleCall;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
//import com.qiscus.sdk.Qiscus;

public class MainActivity extends AppCompatActivity {
    private Button simple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkUpdate();
        simple = findViewById(R.id.btn_simple);
        simple.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SimpleCall.class);
            startActivity(intent);
        });

    }

    private void checkUpdate() {
        RetrofitClient.get().getVersion().enqueue(new Callback<VersionResponse>() {
            @Override
            public void onResponse(Call<VersionResponse> call, Response<VersionResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        VersionResponse versionResponse = new VersionResponse();
                        versionResponse = response.body();
                        if (!versionResponse.getData().get(0).getVersion().equals(BuildConfig.VERSION_NAME)) {
                            startActivity(new Intent(MainActivity.this, UpdateActivity.class));
                            finish();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<VersionResponse> call, Throwable throwable) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
