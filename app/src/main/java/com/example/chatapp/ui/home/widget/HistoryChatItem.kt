package com.example.chatapp.ui.home.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.data.model.UserData

@Composable
fun HistoryChatItem(userData: UserData, lastMessage: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(stringResource(id = R.string.tag_history_chat_item)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = userData.profilePictureUrl,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(60.dp)
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                .clip(CircleShape),
        )

        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = userData.username ?: "",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag(stringResource(id = R.string.tag_username_chat_item))
            )
            Text(
                text = lastMessage,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}