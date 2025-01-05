package com.example.chatapp.ui.call

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import io.getstream.video.android.compose.permission.LaunchCallPermissions
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.activecall.CallContent
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.call.state.LeaveCall
import kotlinx.coroutines.launch

@Composable
fun CallScreen(client: StreamVideo, callId: String, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

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