package com.sleeplessdog.ardraw

import android.app.Application
import android.content.Context
import com.sleeplessdog.ardraw.draw.di.dataModule
import com.sleeplessdog.ardraw.draw.di.domainModule
import com.sleeplessdog.ardraw.draw.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        startKoin {
            androidContext(this@App)
            modules(dataModule, domainModule, presentationModule)
        }

       /* val settingsInteractor: SettingsInteractor = getKoin().get()
        val isNightModeOn = settingsInteractor.getThemeSettings()
        AppCompatDelegate.setDefaultNightMode(
            if (isNightModeOn) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )*/

    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}