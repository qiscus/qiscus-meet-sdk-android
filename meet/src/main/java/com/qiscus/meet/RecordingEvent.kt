package com.qiscus.meet

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**Created by Gustu Maulana Firmansyah on 12,August,2021  gustumaulanaf@gmail.com **/
@Parcelize
data class RecordingEvent (
    var remaining_quota: Int? = 0,
    var status: String? = "null",
    var total_quota: Int? = 0,
    var message: String? = "null"):Parcelable
