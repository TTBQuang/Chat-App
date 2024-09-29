package com.example.chatapp.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.chatapp.R
import com.example.chatapp.data.network.UserData
import com.example.chatapp.ui.theme.ChatAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userData: UserData,
    onSeeProfile: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.padding(8.dp),
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = userData.profilePictureUrl,
                            contentDescription = stringResource(id = R.string.cd_profile_image),
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { onSeeProfile() }
                                .testTag(stringResource(id = R.string.cd_profile_image)),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.avatar),
                        )
                        Spacer(Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                )
                                .clip(CircleShape)
                                .clickable { }
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Create,
                                contentDescription = "Create",
                                modifier = Modifier.size(35.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchBar()
            ChatList()
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    val searchQuery = remember { mutableStateOf(TextFieldValue("")) }

    OutlinedTextField(
        value = searchQuery.value,
        onValueChange = { searchQuery.value = it },
        placeholder = {
            Text(text = "Tìm kiếm", color = Color.Gray)
        },
        modifier = modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray
            )
        }
    )
}

@Composable
fun ChatList(modifier: Modifier = Modifier) {
    val items = listOf(
        "Item 1" to "Subtext 1",
        "Item 2" to "Subtext 2",
        "Item 3" to "Subtext 3",
        "Item 1" to "Subtext 1",
        "Item 2" to "Subtext 2",
        "Item 3" to "Subtext 3",
        "Item 1" to "Subtext 1",
        "Item 2" to "Subtext 2",
        "Item 3" to "Subtext 3",
        "Item 1" to "Subtext 1",
        "Item 2" to "Subtext 2",
        "Item 3" to "Subtext 3"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(items) { item ->
            ChatItem(imageRes = R.drawable.avatar, title = item.first, subtitle = item.second)
        }
    }
}

@Composable
fun ChatItem(imageRes: Int, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(60.dp)
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                .clip(CircleShape),
        )

        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ProfileDialog(
    userData: UserData,
    onDismissRequest: () -> Unit = {},
    onSignOut: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = userData.profilePictureUrl,
                    contentDescription = "Profile picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userData.username ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onSignOut,
                    modifier = Modifier.testTag(stringResource(id = R.string.cd_btn_log_out))
                ) {
                    Text(text = stringResource(id = R.string.sign_out))
                }
            }
        }
    }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    ChatAppTheme {
        Surface {
            HomeScreen(userData = UserData(userId = "1", username = "2", profilePictureUrl = null))
        }
    }
}

@Preview
@Composable
fun PreviewDialog() {
    ChatAppTheme {
        ProfileDialog(userData = UserData(userId = "1", username = "2", profilePictureUrl = null))
    }
}

//@Composable
//fun HorizontalCircleList(modifier: Modifier = Modifier) {
//    val colors = listOf(
//        Color.Red,
//        Color.Green,
//        Color.Blue,
//        Color.Yellow,
//        Color.Cyan,
//        Color.Red,
//        Color.Green,
//        Color.Blue,
//        Color.Yellow,
//    )
//
//    LazyRow(
//        modifier = modifier.padding(vertical = 8.dp),
//        horizontalArrangement = Arrangement.spacedBy(16.dp)
//    ) {
//        items(colors) { color ->
//            CircleItem(color = color, size = 60.dp)
//        }
//    }
//}
//
//@Composable
//fun CircleItem(color: Color, size: Dp) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.width(size)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(size)
//                .background(color, shape = CircleShape)
//        )
//        Text(
//            text = "Quang",
//            modifier = Modifier.width(size),
//            fontSize = 12.sp,
//            textAlign = TextAlign.Center,
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//        )
//    }
//}