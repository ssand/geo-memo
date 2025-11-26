package com.sap.codelab.di

import android.content.Context
import androidx.room.Room
import com.sap.codelab.data.db.MemoDatabase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.sap.codelab.data.repository", "com.sap.codelab.data.datasource")
class DataModule {

    @Single
    fun provideDatabase(applicationContext: Context): MemoDatabase =
        Room.databaseBuilder(applicationContext, MemoDatabase::class.java, "codelab").build()
}
