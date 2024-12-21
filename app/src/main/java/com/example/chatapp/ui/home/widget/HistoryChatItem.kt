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
fun HistoryChatItem(user: UserData, lastMessage: String, onClick: (String) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(user.UID ?: "") }
            .testTag(stringResource(id = R.string.tag_history_chat_item)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.profilePictureUrl,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(60.dp)
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                .clip(CircleShape),
        )

        Text(
            text = user.username ?: "",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp).testTag(stringResource(id = R.string.tag_username_chat_item))
        )
    }
}