package com.example.chatapp.ui.chat

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.helper.fakeUserData
import com.example.chatapp.ui.chat.widget.ChatBottomBar
import com.example.chatapp.ui.chat.widget.ChatTopBar
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.ui.theme.chatItemBackgroundColor

@Composable
fun ChatScreen(modifier: Modifier = Modifier, onNavigateBack: () -> Unit = {}) {
    Scaffold(
        modifier = modifier.navigationBarsPadding(),
        topBar = {
            ChatTopBar(userData = fakeUserData, onBackClick = onNavigateBack)
        },
        bottomBar = {
            ChatBottomBar(
                onSendClick = { },
                onAttachClick = { }
            )
        }
    ) { innerPadding ->
        //ItemList(modifier = Modifier.padding(innerPadding))
        ChatUserInfo(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun ChatUserInfo(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = fakeUserData.profilePictureUrl,
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.avatar),
        )
        Text(
            text = fakeUserData.username ?: "",
            modifier = Modifier.padding(top = 8.dp),
            fontSize = 25.sp,
        )
    }
}

@Composable
fun ItemList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxHeight()
    ) {
        items(20) { index ->
            ChatItem(index = index)
        }
    }
}

@Composable
fun ChatItem(index: Int) {
    val paddingChatItem = LocalConfiguration.current.screenWidthDp.dp / 5

    val alignment = if (index % 2 == 0) {
        Alignment.CenterEnd
    } else {
        Alignment.CenterStart
    }

    Row(verticalAlignment = Alignment.Top) {
        if (index % 2 == 0)
            AsyncImage(
                model = fakeUserData.profilePictureUrl,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
                    .size(32.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.avatar),
            )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (index % 2 == 0) 12.dp else paddingChatItem,
                    end = if (index % 2 == 0) paddingChatItem else 12.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
                .wrapContentSize(alignment)
                .background(
                    color = if (index % 2 == 0)
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    else
                        chatItemBackgroundColor,
                    shape = RoundedCornerShape(12.dp),
                )
        ) {
            Text(
                text = "Item Item Item Item Item Item Item Item Item $index",
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun PreviewChatScreen() {
    ChatAppTheme {
        Surface {
            ChatScreen()
        }
    }
}