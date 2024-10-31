package com.wahyusembiring.habit.di

import com.wahyusembiring.data.repository.AuthRepository
import com.wahyusembiring.data.repository.EventRepository
import com.wahyusembiring.data.repository.LecturerRepository
import com.wahyusembiring.data.repository.MainRepository
import com.wahyusembiring.data.repository.SubjectRepository
import com.wahyusembiring.data.repository.ThesisRepository
import com.wahyusembiring.data.repository.implementation.AuthRepositoryImpl
import com.wahyusembiring.data.repository.implementation.EventRepositoryImpl
import com.wahyusembiring.data.repository.implementation.LecturerRepositoryImpl
import com.wahyusembiring.data.repository.implementation.MainRepositoryImpl
import com.wahyusembiring.data.repository.implementation.SubjectRepositoryImpl
import com.wahyusembiring.data.repository.implementation.ThesisRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl): SubjectRepository

    @Binds
    abstract fun bindLectureRepository(impl: LecturerRepositoryImpl): LecturerRepository

    @Binds
    abstract fun bindThesisRepository(impl: ThesisRepositoryImpl): ThesisRepository

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindMainRepository(impl: MainRepositoryImpl): MainRepository
}