package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.data.model.SignInResult
import com.example.chatapp.data.model.UserData
import com.example.chatapp.helper.FACEBOOK_LOGIN_REQUEST_CODE
import com.example.chatapp.ui.chat.ChatScreen
import com.example.chatapp.ui.theme.ChatAppTheme
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var callbackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

        firebaseAuth = FirebaseAuth.getInstance()
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()

        enableEdgeToEdge()
        setContent {
            ChatAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    MainNavigation(navController = navController, activity = this)
                }
            }
        }
    }

    suspend fun handleFacebookAccessToken(token: AccessToken): SignInResult {
        val credential = FacebookAuthProvider.getCredential(token.token)
        return try {
            val user = firebaseAuth.signInWithCredential(credential).await().user

            SignInResult(
                data = user?.run {
                    UserData(
                        uid = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    suspend fun launchFacebookLogin(): SignInResult {
        val loginManager = LoginManager.getInstance()
        return suspendCoroutine { continuation ->
            loginManager.logInWithReadPermissions(
                this,
                listOf("email", "public_profile")
            )

            loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val accessToken = result.accessToken
                    CoroutineScope(Dispatchers.Main).launch {
                        val signInResult = handleFacebookAccessToken(accessToken)
                        continuation.resume(signInResult)
                    }
                }

                override fun onCancel() {
                    continuation.resume(SignInResult(data = null, errorMessage = "Login cancelled"))
                }

                override fun onError(error: FacebookException) {
                    continuation.resume(SignInResult(data = null, errorMessage = error.message))
                }
            })
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FACEBOOK_LOGIN_REQUEST_CODE) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }
}
