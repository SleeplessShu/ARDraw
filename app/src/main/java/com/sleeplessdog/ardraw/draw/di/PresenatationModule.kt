package com.sleeplessdog.ardraw.draw.di

import android.os.Handler
import android.os.Looper
import com.sleeplessdog.ardraw.draw.presentation.DrawViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel {
        DrawViewModel(get())
    }

    factory<Handler> {
        Handler(Looper.getMainLooper())
    }
}