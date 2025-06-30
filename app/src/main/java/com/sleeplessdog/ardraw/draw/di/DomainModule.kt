package com.sleeplessdog.ardraw.draw.di

import com.sleeplessdog.ardraw.draw.data.ImageRepositoryImpl
import com.sleeplessdog.ardraw.draw.domain.ImageInteractor
import com.sleeplessdog.ardraw.draw.domain.ImageInteractorImpl
import com.sleeplessdog.ardraw.draw.domain.ImageRepository
import org.koin.dsl.module

val domainModule = module {

    single<ImageInteractor> {
        ImageInteractorImpl(get())
    }
}