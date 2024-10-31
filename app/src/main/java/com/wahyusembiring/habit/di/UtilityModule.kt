package com.wahyusembiring.habit.di

import android.app.Application
import com.wahyusembiring.data.local.Converter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {

    @Provides
    @Singleton
    fun provideTypeConverter(application: Application): Converter {
        return Converter(application.applicationContext)
    }

}