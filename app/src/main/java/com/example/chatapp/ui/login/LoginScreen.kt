package com.example.chatapp.ui.login

import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.chatapp.R
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onGoogleButtonCLick: () -> Unit = {},
    onFacebookButtonClick: () -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                AppLogo(modifier = Modifier.size(200.dp))
                Column(
                    modifier = Modifier
                        .width(350.dp)
                        .weight(1f)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.ask_user_login),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 30.dp)
                    )
                    GoogleLogInButton(
                        state = state,
                        modifier = Modifier.padding(bottom = 20.dp),
                        onClick = onGoogleButtonCLick
                    )
                    FacebookLoginButton(onClick = onFacebookButtonClick)
                }
            }
        }
    }
}

@Composable
fun FacebookLoginButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .clickable(onClick = onClick)
            .background(color = Color(0xFF38569E), shape = RoundedCornerShape(6.dp))
            .padding(start = 8.dp, bottom = 12.dp, top = 12.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.facebook_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
                    .padding(end = 8.dp)
            )
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = stringResource(id = R.string.sign_in_with_facebook),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}

@Composable
fun GoogleLogInButton(state: SignInState, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val context = LocalContext.current
    LaunchedEffect(key1 = state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Box(
        modifier = modifier
            .background(color = Color.White, shape = RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(start = 12.dp, bottom = 12.dp, top = 12.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.google_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(35.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.sign_in_with_google),
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Preview
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}