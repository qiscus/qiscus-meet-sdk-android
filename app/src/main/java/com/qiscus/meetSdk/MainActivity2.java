package com.qiscus.meetSdk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.qiscus.meet.QiscusMeet;

/**
 * Created on : 07/08/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QiscusMeet.launch()
                .setRoomId("ajsdnf")
                .setType(QiscusMeet.Type.VOICE)
                .build(this);
    }
}
