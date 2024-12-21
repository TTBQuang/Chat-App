package com.example.chatapp.ui.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.model.ChatRoom
import com.example.chatapp.data.model.Message
import com.example.chatapp.data.model.UserData
import com.example.chatapp.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {
    var chatUiState: ChatUiState by mutableStateOf(ChatUiState())
        private set

    fun fetchPartnerInfo(partnerId: String) {
        viewModelScope.launch {
            val partner = chatRepository.getPartnerInfo(partnerId)
            chatUiState = chatUiState.copy(partner = partner)
        }
    }

    fun fetchAndCreateChatRoom(user: UserData, partner: UserData) {
        // Sử dụng viewModelScope để launch coroutine
        viewModelScope.launch {
            // Thu thập dữ liệu từ Flow được trả về từ chatRepository.fetchAndCreateChatRoom
            chatRepository.fetchAndCreateChatRoom(userUID = user.UID!!, partnerUID = partner.UID!!)
                .collect { chatRoom ->
                    // Log chatRoom và messages sau khi thu thập được
                    Log.d("aaa", "chatRoom: ${chatRoom.id}, messages: ${chatRoom.messages}")

                    // Cập nhật state của UI với chatRoom mới
                    chatUiState = chatUiState.copy(chatRoom = chatRoom)
                }
        }
    }

    fun sendMessage(user: UserData, partner: UserData, message: Message) {
        viewModelScope.launch {
            chatRepository.sendMessage(chatUiState.chatRoom!!.id, message)
        }
    }
}

data class ChatUiState(
    val user: UserData? = null,
    val partner: UserData? = null,
    val chatRoom: ChatRoom? = null
)