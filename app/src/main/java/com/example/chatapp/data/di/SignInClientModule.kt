package com.example.chatapp.data.di

import android.content.Context
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SignInClientModule {
    @Provides
    @Singleton
    fun provideSIgnInClient(@ApplicationContext context: Context): SignInClient {
        return Identity.getSignInClient(context)
    }
}