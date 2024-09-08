package com.example.chatapp

import androidx.activity.ComponentActivity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.ui.home.HomeScreen
import com.example.chatapp.ui.login.LoginScreen
import com.example.chatapp.ui.login.LoginViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Home

@Composable
fun MainNavigation(activity: MainActivity) {
    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val loginViewModel: LoginViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            val state by loginViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(key1 = Unit) {
                if (loginViewModel.getSignedInUser() != null) {
                    navController.navigate(route = Home)
                }
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == RESULT_OK) {
                        coroutineScope.launch {
                            val signInResult = loginViewModel.signInGoogleWithIntent(
                                intent = result.data ?: return@launch
                            )
                            loginViewModel.onSignInResult(signInResult)
                        }
                    }
                }
            )

            LaunchedEffect(key1 = state.isSignInSuccessful) {
                if (state.isSignInSuccessful) {
                    navController.navigate(route = Home)
                    loginViewModel.resetState()
                }
            }

            LoginScreen(
                viewModel = loginViewModel,
                onGoogleButtonCLick = {
                    coroutineScope.launch {
                        loginViewModel.setLoading(true)
                        val signInIntentSender = loginViewModel.startGoogleSignIn()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                },
                onFacebookButtonClick = {
                    coroutineScope.launch {
                        loginViewModel.setLoading(true)
                        val signInResult = activity.launchFacebookLogin()
                        loginViewModel.onSignInResult(signInResult)
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen(
                userData = loginViewModel.getSignedInUser(),
                onSignOut = {
                    coroutineScope.launch {
                        loginViewModel.signOut()
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
