package de.malteans.digishelf

import android.app.Application
import de.malteans.digishelf.di.androidAppModule
import de.malteans.digishelf.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(androidAppModule) {
            androidContext(this@MainApplication)
        }
    }
}