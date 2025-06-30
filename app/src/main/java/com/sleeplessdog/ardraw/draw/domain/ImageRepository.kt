package com.sleeplessdog.ardraw.draw.domain

import android.net.Uri

interface ImageRepository {
    fun saveImageToPrivateStorage(uri: Uri): Uri?
}