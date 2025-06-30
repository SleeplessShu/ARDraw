package com.sleeplessdog.ardraw.draw.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sleeplessdog.ardraw.draw.domain.DrawMessageState
import com.sleeplessdog.ardraw.draw.domain.ImageInteractor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.sleeplessdog.ardraw.utils.Constants

class DrawViewModel(
    private val imageInteractor: ImageInteractor
) : ViewModel() {

    private val _playlistImage = MutableLiveData(Constants.DEFAULT_IMAGE_URI)
    val playlistImage: LiveData<Uri> get() = _playlistImage

    val toastMessage = MutableSharedFlow<DrawMessageState>(extraBufferCapacity = 1)

    fun saveImageToPrivateStorage(uri: Uri?) {
        if (uri == null) {
            viewModelScope.launch {
                toastMessage.emit(DrawMessageState.IMAGE_NOT_SELECTED)
            }
        } else {
            val savedUri = imageInteractor.saveImageToPrivateStorage(uri)
            _playlistImage.postValue(savedUri ?: _playlistImage.value)
        }
    }
    fun accessToStorageGranted() {
        viewModelScope.launch {
            toastMessage.emit(DrawMessageState.ACCESS_GRANTED)
        }
    }

    fun onPermissionDenied(isPermissionsDenied: Boolean) {
        viewModelScope.launch {
            if (isPermissionsDenied) {
                toastMessage.emit(DrawMessageState.CHECK_SETTINGS_FOR_ACCESS)
            } else {
                toastMessage.emit(DrawMessageState.PERMISSION_DECLINED)
            }
        }
    }
}