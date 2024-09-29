package com.example.chatapp.repository

import android.content.Intent
import android.content.IntentSender
import com.example.chatapp.data.network.SignInResult
import com.example.chatapp.data.network.UserData
import com.example.chatapp.data.repository.SignInRepository
import com.example.chatapp.authenticatedFakeSignInResult
import com.example.chatapp.fakeUserData
import com.example.chatapp.unauthenticatedFakeSignInResult
import javax.inject.Inject

class AuthenticatedFakeSignInRepository @Inject constructor() : SignInRepository {
    private var isLoggedOut = false

    override suspend fun startGoogleSignIn(): IntentSender? {
        return null
    }

    override suspend fun signInGoogleWithIntent(intent: Intent): SignInResult {
        return authenticatedFakeSignInResult
    }

    override suspend fun signOut() {

    }

    override fun getSignedInUser(): UserData? {
        if (!isLoggedOut) {
            // This function is called for the first time
            isLoggedOut = true
            return fakeUserData
        } else {
            // This function is called for the second or more time, when user has already logged out
            return null
        }
    }

    override fun saveUserUid(): String {
        return ""
    }
}

class UnauthenticatedFakeSignInRepository @Inject constructor() : SignInRepository {
    override suspend fun startGoogleSignIn(): IntentSender? {
        return null
    }

    override suspend fun signInGoogleWithIntent(intent: Intent): SignInResult {
        return unauthenticatedFakeSignInResult
    }

    override suspend fun signOut() {

    }

    override fun getSignedInUser(): UserData? {
        return null
    }

    override fun saveUserUid(): String {
        return "errorMessage"
    }
}