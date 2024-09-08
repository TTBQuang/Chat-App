package com.example.chatapp.data.di

import com.example.chatapp.data.repository.SignInRepository
import com.example.chatapp.data.repository.SignInRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SignInRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindSignInRepository(
        signInRepositoryImpl: SignInRepositoryImpl
    ): SignInRepository
}