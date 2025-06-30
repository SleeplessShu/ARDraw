package com.sleeplessdog.ardraw.draw.di

import com.sleeplessdog.ardraw.draw.data.ImageRepositoryImpl
import com.sleeplessdog.ardraw.draw.domain.ImageRepository
import org.koin.dsl.module

val dataModule = module {

    single<ImageRepository> {
        ImageRepositoryImpl(get())
    }
}