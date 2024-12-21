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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.data.model.Message
import com.example.chatapp.data.model.UserData
import com.example.chatapp.helper.fakeUserData
import com.example.chatapp.ui.chat.widget.ChatBottomBar
import com.example.chatapp.ui.chat.widget.ChatTopBar
import com.example.chatapp.ui.theme.ChatAppTheme
import com.example.chatapp.ui.theme.chatItemBackgroundColor

@Composable
fun ChatScreen(
    user: UserData,
    partnerId: String,
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val state = chatViewModel.chatUiState

    LaunchedEffect(partnerId) {
        chatViewModel.fetchPartnerInfo(partnerId)
    }

    LaunchedEffect(state.partner) {
        state.partner?.let { chatViewModel.fetchAndCreateChatRoom(user, it) }
    }

    if (state.partner == null) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.testTag(stringResource(id = R.string.tag_CircularProgressIndicator)))
        }
    } else {
        Scaffold(
            modifier = modifier.navigationBarsPadding(),
            topBar = {
                ChatTopBar(userData = state.partner, onBackClick = onNavigateBack)
            },
            bottomBar = {
                ChatBottomBar(
                    onSendClick = {
                        chatViewModel.sendMessage(
                            user,
                            state.partner,
                            Message(content = it, sender = user.UID ?: "")
                        )
                    },
                )
            }
        ) { innerPadding ->
            if (state.chatRoom?.messages?.isEmpty() == false) {
                ChatList(
                    partner = state.partner,
                    messages = state.chatRoom.messages,
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                ChatUserInfo(
                    partner = state.partner,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun ChatUserInfo(partner: UserData, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = partner.profilePictureUrl,
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.avatar),
        )
        Text(
            text = partner.username ?: "",
            modifier = Modifier.padding(top = 8.dp),
            fontSize = 25.sp,
        )
    }
}

@Composable
fun ChatList(partner: UserData, messages: List<Message>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxHeight()
    ) {
        items(messages.size) { index ->
            ChatItem(message = messages[index], partner = partner)
        }
    }
}

@Composable
fun ChatItem(message: Message, partner: UserData) {
    val paddingChatItem = LocalConfiguration.current.screenWidthDp.dp / 5

    val alignment = if (message.sender != partner.UID) {
        Alignment.CenterEnd
    } else {
        Alignment.CenterStart
    }

    Row(verticalAlignment = Alignment.Top) {
        if (message.sender == partner.UID)
            AsyncImage(
                model = partner.profilePictureUrl,
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
                    start = if (message.sender == partner.UID) 12.dp else paddingChatItem,
                    end = if (message.sender == partner.UID) paddingChatItem else 12.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
                .wrapContentSize(alignment)
                .background(
                    color = if (message.sender != partner.UID)
                        MaterialTheme.colorScheme.surfaceContainerHigh
                    else
                        chatItemBackgroundColor,
                    shape = RoundedCornerShape(12.dp),
                )
        ) {
            Text(
                text = message.content,
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
            ChatScreen(user = fakeUserData, partnerId = fakeUserData.UID ?: "")
        }
    }
}