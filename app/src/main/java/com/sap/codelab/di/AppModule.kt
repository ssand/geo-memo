package com.sap.codelab.di

import com.sap.codelab.view.create.di.CreateViewModelModule
import com.sap.codelab.view.detail.di.DetailViewModelModule
import com.sap.codelab.view.home.di.HomeViewModelModule
import org.koin.core.annotation.Module

@Module(
    includes = [
        DataModule::class,
        CreateViewModelModule::class,
        DetailViewModelModule::class,
        HomeViewModelModule::class
    ]
)
class AppModule
