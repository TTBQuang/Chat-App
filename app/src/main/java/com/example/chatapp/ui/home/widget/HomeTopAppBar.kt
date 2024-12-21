package com.example.chatapp.ui.home.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.data.model.UserData

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeTopAppBar(
    userData: UserData,
    onSeeProfile: () -> Unit
) {
    TopAppBar(
        title = {},
        actions = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = userData.profilePictureUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .clickable { onSeeProfile() }
                        .testTag(stringResource(id = R.string.tag_profile_image)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.avatar),
                )
            }
        }
    )
}