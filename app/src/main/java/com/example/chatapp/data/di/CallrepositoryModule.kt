package com.example.chatapp.data.di

import com.example.chatapp.data.repository.CallRepository
import com.example.chatapp.data.repository.CallRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CallRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindCallRepository(
        callRepositoryImpl: CallRepositoryImpl
    ): CallRepository
}