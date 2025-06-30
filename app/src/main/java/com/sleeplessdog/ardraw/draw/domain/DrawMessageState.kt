package com.sleeplessdog.ardraw.draw.domain

enum class DrawMessageState {
    PERMISSION_DECLINED,
    ACCESS_GRANTED,
    EDITS_SAVED,
    CHECK_SETTINGS_FOR_ACCESS,
    IMAGE_NOT_SELECTED,
}