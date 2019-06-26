package com.qiscus.meet.model

import android.os.Parcelable
import com.qiscus.meet.QiscusMeet
import kotlinx.android.parcel.Parcelize

/**
 * Created on : 14/06/19
 * Author     : Taufik Budi S
 * GitHub     : https://github.com/tfkbudi
 */
@Parcelize
data class Call(
    val roomId: String,
    val calleeUsername: String,
    val calleeDisplayName: String,
    val calleeAvatar: String,
    val callerUsername: String,
    val callerDisplayName: String,
    val callerAvatar: String,
    val callType: QiscusMeet.CallType,
    val callAs: QiscusMeet.CallAs
) : Parcelable