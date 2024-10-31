package com.wahyusembiring.habit.di

import android.app.Application
import com.wahyusembiring.data.repository.DataStoreRepository
import com.wahyusembiring.data.repository.implementation.DataStoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideDataStoreRepository(application: Application): DataStoreRepository {
        return DataStoreRepositoryImpl(application.applicationContext)
    }

}