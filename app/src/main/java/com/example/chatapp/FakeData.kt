package com.example.chatapp

import com.example.chatapp.data.network.SignInResult
import com.example.chatapp.data.network.UserData

val fakeUserData = UserData(userId = "1", username = "2", profilePictureUrl = null)

val authenticatedFakeSignInResult = SignInResult(errorMessage = null, data = fakeUserData)
val unauthenticatedFakeSignInResult = SignInResult(errorMessage = "fakeErrorMessage", data = null)