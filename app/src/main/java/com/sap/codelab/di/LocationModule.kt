package com.sap.codelab.di

import android.content.Context
import com.sap.codelab.location.GeofenceScheduler
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class LocationModule {

    @Single
    fun provideGeofenceScheduler(context: Context) =
        GeofenceScheduler(context)
}
