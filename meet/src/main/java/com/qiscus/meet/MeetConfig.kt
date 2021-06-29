package com.qiscus.meet

class MeetConfig(){
    private var enablePassword: Boolean = false
    private var enableChat: Boolean = false
    private var overflowMenu: Boolean = false
    private var videoThumbnailsOn: Boolean = true
    private var enableRoomName: Boolean = true
    private var jwtPayload: String = ""
    private var jwtConfig = MeetJwtConfig()

    fun setPassword(password: Boolean) = apply { this.enablePassword = password }
    fun setChat(chat: Boolean) = apply { this.enableChat = chat }
    fun setVideoThumbnailsOn(videoThumbnailsOn: Boolean) = apply { this.videoThumbnailsOn = videoThumbnailsOn }
    fun setOverflowMenu(overflowMenu: Boolean) = apply { this.overflowMenu = overflowMenu }
    fun setEnableRoomName(enableRoomName: Boolean) = apply { this.enableRoomName = enableRoomName }
    fun setJwtPayload(jwtPayload: String) = apply {this.jwtPayload = jwtPayload}

    fun setJwtConfig(jwtConfig: MeetJwtConfig) = apply {
        this.jwtConfig = jwtConfig
    }

    fun getPassword(): Boolean {
        return this.enablePassword
    }
    fun getChat(): Boolean {
        return this.enableChat
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
}