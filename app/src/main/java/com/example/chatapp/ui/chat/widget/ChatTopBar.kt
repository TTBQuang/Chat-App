package com.example.chatapp.ui.chat.widget

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.data.model.UserData
import com.example.chatapp.ui.theme.iconChatTopBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    userData: UserData,
    partnerId: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onCallIconClick: (String, String) -> Unit = { _, _ -> },
) {
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxHeight(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = userData.profilePictureUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.avatar),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = userData.username ?: "",
                    fontSize = 20.sp,
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag(stringResource(id = R.string.tag_back_icon_chat_screen))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = iconChatTopBarColor,
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    onCallIconClick(userData.UID ?: "", partnerId) },
            ) {
                Icon(
                    Icons.Default.Call,
                    contentDescription = "Call",
                    tint = iconChatTopBarColor,
                )
            }
        },
        modifier = modifier,
    )
}
