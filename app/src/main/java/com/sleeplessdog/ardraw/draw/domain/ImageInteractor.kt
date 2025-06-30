package com.sleeplessdog.ardraw.draw.domain

import android.net.Uri

interface ImageInteractor {
    fun saveImageToPrivateStorage(uri: Uri): Uri?
}