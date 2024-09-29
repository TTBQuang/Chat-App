package com.example.chatapp.ui.login

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.network.SignInResult
import com.example.chatapp.data.network.UserData
import com.example.chatapp.data.repository.SignInRepository
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInRepository: SignInRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    private var _userData: UserData? = null
    val userData: UserData?
        get() {
            if (_userData == null) {
                _userData = getSignedInUser()
            }
            return _userData
        }

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage,
            )
        }
        if (result.data != null) {
            saveUserUid()
        } else {
            setLoading(false)
        }
    }

    private fun saveUserUid() {
        val errorMessage = signInRepository.saveUserUid()
        if (errorMessage.isEmpty()) {
            _state.update {
                it.copy(
                    isLoading = false
                )
            }
        } else {
            _state.update {
                it.copy(
                    isSignInSuccessful = false,
                    signInError = errorMessage,
                    isLoading = false
                )
            }
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }

    suspend fun startGoogleSignIn(): IntentSender? {
        return signInRepository.startGoogleSignIn()
    }

    suspend fun signOut() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.providerData?.forEach { userInfo ->
            when (userInfo.providerId) {
                FacebookAuthProvider.PROVIDER_ID -> {
                    FirebaseAuth.getInstance().signOut()
                }

                GoogleAuthProvider.PROVIDER_ID -> {
                    signInRepository.signOut()
                }
            }
        }

        _userData = null
    }

    suspend fun signInGoogleWithIntent(intent: Intent): SignInResult {
        return signInRepository.signInGoogleWithIntent(intent)
    }

    private fun getSignedInUser(): UserData? {
        return signInRepository.getSignedInUser()
    }

    fun setLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }
}

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isLoading: Boolean = false,
)