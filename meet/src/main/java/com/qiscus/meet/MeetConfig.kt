package com.qiscus.meet

class MeetConfig() {
    private var enablePassword: Boolean = false
    private var enableChat: Boolean = false
    private var overflowMenu: Boolean = false
    private var videoThumbnailsOn: Boolean = true
    private var enableRoomName: Boolean = true
    private var jwtPayload: String = ""
    private var jwtConfig = MeetJwtConfig()
    private var screenSharing: Boolean = false
    private var enableBackPressed: Boolean = false
    private var autoRecording: Boolean = false
    private var recording: Boolean = false
    private var reactions: Boolean = false
    private var raiseHand: Boolean = false
    private var securityOptions: Boolean = true
    private var toolbox: Boolean = true
    private var toolboxAlwaysVisible: Boolean = true
    private var tileView: Boolean = true
    private var participantMenu: Boolean = true
    private var videoMuteButton: Boolean = true
    private var audioMuteButton: Boolean = true

    fun setEnableBackPressed(enableBackPressed: Boolean) =
        apply { this.enableBackPressed = enableBackPressed }

    fun setAutoRecording(autoRecording: Boolean) =
        apply { this.autoRecording = autoRecording }

    fun setRecording(recording: Boolean) = apply { this.recording = recording }

    fun setScreenSharing(screenSharing: Boolean) = apply { this.screenSharing = screenSharing }
    fun setPassword(password: Boolean) = apply { this.enablePassword = password }
    fun setChat(chat: Boolean) = apply { this.enableChat = chat }
    fun setVideoThumbnailsOn(videoThumbnailsOn: Boolean) =
        apply { this.videoThumbnailsOn = videoThumbnailsOn }

    fun setOverflowMenu(overflowMenu: Boolean) = apply { this.overflowMenu = overflowMenu }
    fun setEnableRoomName(enableRoomName: Boolean) = apply { this.enableRoomName = enableRoomName }
    fun setReactions(reactionsEnabled: Boolean) = apply { this.reactions = reactionsEnabled }
    fun setRaiseHand(raiseHandEnabled: Boolean) = apply { this.raiseHand = raiseHandEnabled }
    fun setSecurityOptions(securityOptionsEnabled: Boolean) = apply { this.securityOptions = securityOptionsEnabled }
    fun setToolbox(toolboxEnabled: Boolean) = apply { this.toolbox = toolboxEnabled }
    fun setToolboxAlwaysVisible(toolboxAlwaysVisible: Boolean) = apply { this.toolboxAlwaysVisible = toolboxAlwaysVisible }
    fun setTileView(tileViewEnabled: Boolean) = apply { this.tileView = tileViewEnabled }
    fun setParticipantMenu(participantMenuEnabled: Boolean) = apply { this.participantMenu = participantMenuEnabled }
    fun setVideoMuteButton(videoMuteButtonEnabled: Boolean) = apply { this.videoMuteButton = videoMuteButtonEnabled }
    fun setAudioMuteButton(audioMuteButtonEnabled: Boolean) = apply { this.audioMuteButton = audioMuteButtonEnabled }
    fun setJwtPayload(jwtPayload: String) = apply { this.jwtPayload = jwtPayload }

    fun setJwtConfig(jwtConfig: MeetJwtConfig) = apply {
        this.jwtConfig = jwtConfig
    }

    fun getPassword(): Boolean {
        return this.enablePassword
    }

    fun getChat(): Boolean {
        return this.enableChat
    }

    fun getAutoRecording(): Boolean {
        return this.autoRecording
    }

    fun getRecording() = recording

    fun getScreenSharing(): Boolean {
        return this.screenSharing
    }

    fun getEnableBackPress(): Boolean {
        return this.enableBackPressed
    }

    fun getOverflowMenu(): Boolean {
        return this.overflowMenu
    }

    fun getVideoThumbnailsOn(): Boolean {
        return this.videoThumbnailsOn
    }

    fun getJwtPayload(): String {
        return this.jwtPayload
    }

    fun getJwtConfig(): MeetJwtConfig {
        return this.jwtConfig
    }

    fun isEnableRoomName(): Boolean {
        return this.enableRoomName
    }

    fun isEnableReactions() = reactions

    fun isEnableRaiseHand() = raiseHand

    fun isEnableSecurityOptions() = securityOptions

    fun isEnableToolbox() = toolbox

    fun toolboxAlwaysVisible() = toolboxAlwaysVisible

    fun isEnableTileView() = tileView

    fun isEnableParticipantMenu() = participantMenu

    fun isEnableVideoMuteButton() = videoMuteButton

    fun isEnableAudioMuteButton() = audioMuteButton
}