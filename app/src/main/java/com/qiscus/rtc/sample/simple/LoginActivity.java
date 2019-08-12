package com.qiscus.rtc.sample.simple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.qiscus.rtc.sample.R;

/**
 * Created by fitra on 09/02/18.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText username;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        register = (Button) findViewById(R.id.register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!username.getText().toString().isEmpty()) {
//                    QiscusRtc.register(username.getText().toString(), username.getText().toString(), "http://dk6kcyuwrpkrj.cloudfront.net/wp-content/uploads/sites/45/2014/05/avatar-blank.jpg");
                    Intent intent = new Intent(LoginActivity.this, CallActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Username required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
