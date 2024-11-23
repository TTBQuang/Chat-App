package com.example.chatapp.ui.home

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chatapp.data.model.UserData
import com.example.chatapp.ui.home.widget.HistoryChatList
import com.example.chatapp.ui.home.widget.HomeTopAppBar
import com.example.chatapp.ui.home.widget.ProfileDialog
import com.example.chatapp.ui.home.widget.SearchBar
import com.example.chatapp.ui.theme.ChatAppTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel = hiltViewModel(),
    userData: UserData,
    onSeeProfile: () -> Unit = {},
    onChatItemClick: () -> Unit = {},
) {
    val state = homeViewModel.homeUiState

    Scaffold(
        modifier = modifier.padding(8.dp),
        topBar = {
            HomeTopAppBar(userData, onSeeProfile)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SearchBar(onSearch = {
                if (it.isEmpty()) {
                    homeViewModel.getAllUsers()
                } else {
                    homeViewModel.findUsersByUsername(it)
                }
            })
            HistoryChatList(userDataList = state.userDataList, onItemClick = onChatItemClick)
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    ChatAppTheme {
        Surface {
            HomeScreen(userData = UserData(uid = "1", username = "2", profilePictureUrl = null))
        }
    }
}

@Preview
@Composable
fun PreviewDialog() {
    ChatAppTheme {
        ProfileDialog(userData = UserData(uid = "1", username = "2", profilePictureUrl = null))
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