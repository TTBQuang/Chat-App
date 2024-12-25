package com.example.chatapp

import android.app.Activity.RESULT_OK
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.chatapp.helper.STREAM_API_KEY
import com.example.chatapp.ui.chat.ChatScreen
import com.example.chatapp.ui.chat.ChatViewModel
import com.example.chatapp.ui.home.HomeScreen
import com.example.chatapp.ui.home.widget.ProfileDialog
import com.example.chatapp.ui.login.LoginScreen
import com.example.chatapp.ui.login.LoginViewModel
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.core.GEO
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.core.call.state.LeaveCall
import io.getstream.video.android.model.User
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object Home

@Serializable
object Profile

@Serializable
data class Chat(val partnerId: String)

@Serializable
data class Call(val userId: String, val partnerId: String)

@Composable
fun MainNavigation(
    activity: MainActivity,
    navController: NavHostController = rememberNavController()
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = hiltViewModel()
    val chatViewModel: ChatViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Login) {
        composable<Login> {
            val state by loginViewModel.state.collectAsStateWithLifecycle()

            LaunchedEffect(key1 = Unit) {
                if (loginViewModel.userData != null) {
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
            loginViewModel.userData?.let { userData ->
                HomeScreen(
                    userData = userData,
                    onSeeProfile = { navController.navigate(route = Profile) },
                    onChatItemClick = {
                        navController.navigate(route = Chat(partnerId = it))
                    }
                )
            }
        }

        dialog<Profile> {
            loginViewModel.userData?.let { userData ->
                ProfileDialog(
                    userData = userData,
                    onDismissRequest = {
                        navController.popBackStack()
                    },
                    onSignOut = {
                        coroutineScope.launch {
                            loginViewModel.signOut()
                            navController.popBackStack(Login, false)
                        }
                    }
                )
            }
        }

        composable<Chat> { backStackEntry ->
            loginViewModel.userData?.let { it1 ->
                ChatScreen(
                    user = it1,
                    partnerId = backStackEntry.toRoute<Chat>().partnerId,
                    onNavigateBack = { navController.popBackStack() },
                    onCall = { userId, partnerId ->
                        navController.navigate(
                            route = Call(
                                userId = userId,
                                partnerId = partnerId
                            )
                        )
                    },
                )
            }
        }

        composable<Call> { backStackEntry ->
            val partnerId = backStackEntry.toRoute<Call>().partnerId
            val userId = backStackEntry.toRoute<Call>().userId
            val callId = if (partnerId < userId) {
                "${partnerId}_$userId"
            } else {
                "${userId}_$partnerId"
            }
            val apiKey = STREAM_API_KEY
            val userToken = chatViewModel.fetchStreamUserToken(userId)
            val user = User(id = userId)

            val client = remember {
                StreamVideoBuilder(
                    context = context,
                    apiKey = apiKey,
                    geo = GEO.GlobalEdgeNetwork,
                    user = user,
                    token = userToken,
                ).build()
            }

            VideoTheme {
                Scaffold { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        val call = client.call(type = "default", id = callId)
                        LaunchCallPermissions(
                            call = call,
                            onAllPermissionsGranted = {
                                coroutineScope.launch {
                                    call.join(create = true)
                                }
                            }
                        )
                        CallContent(
                            enableInPictureInPicture = false,
                            onCallAction = { action ->
                                when (action) {
                                    LeaveCall -> {
                                        coroutineScope.launch {
                                            try {
                                                call.leave()
                                                navController.popBackStack()
                                                Toast.makeText(
                                                    context,
                                                    "Left call successfully",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    context,
                                                    "Error leaving call: ${e.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }

                                    else -> {
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize(),
                            call = call,
                            onBackPressed = {
                                coroutineScope.launch {
                                    try {
                                        call.leave()
                                        navController.popBackStack()
                                        Toast.makeText(
                                            context,
                                            "Left call successfully",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "Error leaving call: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}