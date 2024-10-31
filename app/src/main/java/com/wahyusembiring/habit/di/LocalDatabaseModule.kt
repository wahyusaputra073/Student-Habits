package com.wahyusembiring.habit.di

import android.app.Application
import com.wahyusembiring.data.local.Converter
import com.wahyusembiring.data.local.MainDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseModule {

    @Provides
    @Singleton
    fun provideMainDatabase(
        application: Application,
        converter: Converter
    ): MainDatabase {
        return MainDatabase.getSingleton(
            appContext = application.applicationContext,
            converter = converter
        )
    }

    @Provides
    @Singleton
    fun provideHomeworkDao(mainDatabase: MainDatabase) = mainDatabase.homeworkDao

    @Provides
    @Singleton
    fun provideExamDao(mainDatabase: MainDatabase) = mainDatabase.examDao

    @Provides
    @Singleton
    fun provideReminderDao(mainDatabase: MainDatabase) = mainDatabase.reminderDao

    @Provides
    @Singleton
    fun provideSubjectDao(mainDatabase: MainDatabase) = mainDatabase.subjectDao

    @Provides
    @Singleton
    fun provideTaskDao(mainDatabase: MainDatabase) = mainDatabase.taskDao

    @Provides
    @Singleton
    fun provideThesisDao(mainDatabase: MainDatabase) = mainDatabase.thesisDao

    @Provides
    @Singleton
    fun provideLectureDao(mainDatabase: MainDatabase) = mainDatabase.lectureDao

}