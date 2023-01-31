package com.qiscus.meet

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Options(
    var roomURL: String,
    var audioMuted: Boolean,
    var audioOnly: Boolean,
    var pipEnabled: Boolean,
    var requiredPasswordEnabled: Boolean,
    var chatEnabled: Boolean,
    var overFlowMenuEnabled: Boolean,
    var videoThumbnailEnabled: Boolean,
    var meetingNameEnabled: Boolean,
    var androidScreenSharingEnabled: Boolean,
    var recordingEnabled: Boolean,
    var reactionsEnabled: Boolean,
    var raiseHandEnabled: Boolean,
    var securityOptionsEnabled: Boolean,
    var toolboxEnabled: Boolean,
    var toolboxAlwaysVisible: Boolean,
    var tileViewEnabled: Boolean,
    var participantMenuEnabled: Boolean,
    var videoMuteButtonEnabled: Boolean,
    var audioMuteButtonEnabled: Boolean,
    var token: String,
    var videoMuted: Boolean,
    var autoRecordingEnabled: Boolean
) : Parcelable {
    constructor() : this(
        "",
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        false,
        "",
        false, false
    )
}