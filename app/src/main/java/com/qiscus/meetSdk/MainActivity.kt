package com.qiscus.meetSdk

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.qiscus.meet.QiscusMeet
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnJoin.setOnClickListener {
            val room = etRoom.text.toString()
            if (room.isNotEmpty()) {
                QiscusMeet.launch()
                    .setRoomId(room)
                    .setType(QiscusMeet.Type.VIDEO)
                    .build(this)
            }

        }

    }
}
