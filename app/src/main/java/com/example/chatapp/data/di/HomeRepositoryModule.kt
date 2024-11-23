package com.example.chatapp.data.di

import com.example.chatapp.data.repository.FirebaseHomeRepository
import com.example.chatapp.data.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindHomeRepository(
        firebaseHomeRepository: FirebaseHomeRepository
    ): HomeRepository
}