package com.qiscus.meetSdk

import android.app.Application
import com.qiscus.meet.QiscusMeet

/**
 * Created on : 12/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
class Apps: Application() {

    override fun onCreate() {
        super.onCreate()
        QiscusMeet.init(this, "https://meet.qiscus.com")
    }
}