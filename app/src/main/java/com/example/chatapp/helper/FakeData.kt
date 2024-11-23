package com.example.chatapp.helper

import com.example.chatapp.data.model.SignInResult
import com.example.chatapp.data.model.UserData

val fakeUserData = UserData(uid = "1", username = "fakeUserName", profilePictureUrl = "https://lh3.googleusercontent.com/a/ACg8ocJwU-4Tv7gOOyW-GMC6PgUFnG1WFTAShpS22mhCCotPo1q98Q=s96-c")
val fakeUserDataList = listOf(
    fakeUserData,
    fakeUserData.copy(uid = "2", username = "fakeUserName2"),
    fakeUserData.copy(uid = "3", username = "fakeUserName3"),
    fakeUserData.copy(uid = "4", username = "fakeUserName4"),
)
val fakeSearchUserDataList = listOf(
    fakeUserData,
    fakeUserData.copy(uid = "2", username = "fakeUserName2"),
)

val authenticatedFakeSignInResult = SignInResult(errorMessage = null, data = fakeUserData)
val unauthenticatedFakeSignInResult = SignInResult(errorMessage = "fakeErrorMessage", data = null)