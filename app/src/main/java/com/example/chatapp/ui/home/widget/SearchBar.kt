package com.example.chatapp.ui.home.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.chatapp.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchBar(onSearch: (String) -> Unit, modifier: Modifier = Modifier) {
    val searchQuery = rememberSaveable { mutableStateOf("") }

    LaunchedEffect(searchQuery.value) {
        delay(500)
        onSearch(searchQuery.value)
    }

    OutlinedTextField(
        value = searchQuery.value,
        onValueChange = { newText ->
            searchQuery.value = newText
        },
        placeholder = {
            Text(text = stringResource(id = R.string.search_hint), color = Color.Gray)
        },
        modifier = modifier
            .padding(vertical = 8.dp)
            .testTag(stringResource(id = R.string.tag_search_bar))
            .fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "",
                tint = Color.Gray,
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        )
    )
}
