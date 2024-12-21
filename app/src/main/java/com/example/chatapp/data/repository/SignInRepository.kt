package com.example.chatapp.data.repository

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.chatapp.R
import com.example.chatapp.data.model.SignInResult
import com.example.chatapp.data.model.UserData
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

interface SignInRepository {
    suspend fun startGoogleSignIn(): IntentSender?
    suspend fun signInGoogleWithIntent(intent: Intent): SignInResult
    suspend fun signOut()
    fun getSignedInUser(): UserData?
    fun saveUserInfo(): String
}

class SignInRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val oneTapClient: SignInClient
) : SignInRepository {
    private val auth = Firebase.auth

    override suspend fun startGoogleSignIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildGoogleSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    override suspend fun signInGoogleWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            SignInResult(
                data = user?.run {
                    UserData(
                        UID = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    override suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    override fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            UID = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString()
        )
    }

    override fun saveUserInfo(): String {
        var errorMessage = ""
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser

        val userData = currentUser?.run {
            UserData(
                UID = uid,
                username = displayName,
                profilePictureUrl = photoUrl?.toString()
            )
        }

        if (userData == null) {
            return context.getString(R.string.error_save_UID_user_null)
        }

        val userMap = hashMapOf(
            "UID" to userData.UID,
            "username" to userData.username,
            "profilePictureUrl" to userData.profilePictureUrl
        )

        db.collection("users")
            .whereEqualTo("UID", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // If don't find user with this UID, add to firestore
                    db.collection("users")
                        .add(userMap)
                        .addOnSuccessListener {
                            // success
                        }
                        .addOnFailureListener { e ->
                            errorMessage = e.message.toString()
                        }
                }
            }
            .addOnFailureListener { e ->
                errorMessage = e.message.toString()
            }

        return errorMessage
    }

    private fun buildGoogleSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}