package com.sleeplessdog.ardraw.draw.domain

import android.net.Uri


class ImageInteractorImpl(
    private val imageRepository: ImageRepository
) : ImageInteractor {
    override fun saveImageToPrivateStorage(uri: Uri): Uri? {
        return imageRepository.saveImageToPrivateStorage(uri)
    }
}