package com.sap.codelab

import android.app.Application
import android.content.pm.ApplicationInfo
import com.sap.codelab.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.ksp.generated.module

/**
 * Extension of the Android Application class.
 */
internal class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val logLevel = if ((applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0)
            Level.ERROR
        else
            Level.NONE

        startKoin {
            androidLogger(logLevel)
            androidContext(this@App)
            modules(
                AppModule().module
            )
        }
    }
}
