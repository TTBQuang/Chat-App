package com.example.chatapp.ui.chat.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chatapp.R
import com.example.chatapp.ui.theme.iconChatBottomBarColor

@Composable
fun ChatBottomBar(
    onSendClick: (String) -> Unit,
) {
    val chatText = rememberSaveable { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current // Lấy controller của bàn phím

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.Transparent)
            .imePadding(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        BasicTextField(
            value = chatText.value,
            onValueChange = { newText -> chatText.value = newText },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface),
            decorationBox = { innerTextField ->
                if (chatText.value.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.chat_hint),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        )

        IconButton(
            modifier = Modifier.size(36.dp),
            onClick = {
                onSendClick(chatText.value) // Gọi hàm gửi tin nhắn
                chatText.value = "" // Xóa nội dung trong ô chat
                keyboardController?.hide() // Đóng bàn phím
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = iconChatBottomBarColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
