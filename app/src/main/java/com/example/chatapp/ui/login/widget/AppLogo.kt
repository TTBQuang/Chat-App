package com.example.chatapp.ui.login.widget

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.chatapp.R

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.app_logo),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
    )
}