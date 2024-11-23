package com.example.chatapp.di

import com.example.chatapp.data.di.HomeRepositoryModule
import com.example.chatapp.data.repository.HomeRepository
import com.example.chatapp.repository.FakeHomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [HomeRepositoryModule::class]
)
abstract class FakeHomeRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindHomeRepository(
        fakeHomeRepository: FakeHomeRepository
    ): HomeRepository
}