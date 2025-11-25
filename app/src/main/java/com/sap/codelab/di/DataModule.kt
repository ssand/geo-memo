package com.sap.codelab.di

import android.content.Context
import androidx.room.Room
import com.sap.codelab.data.db.Database
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.sap.codelab.data.repository")
class DataModule {

    @Single
    fun provideDatabase(applicationContext: Context): Database =
        Room.databaseBuilder(applicationContext, Database::class.java, "codelab").build()
}
