package com.example.chatapp.ui.home.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chatapp.data.model.UserData

@Composable
fun HistoryChatList(
    userDataList: List<UserData>,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(userDataList) { item ->
            HistoryChatItem(user = item, lastMessage = "item.second", onClick = onItemClick)
        }
    }
}