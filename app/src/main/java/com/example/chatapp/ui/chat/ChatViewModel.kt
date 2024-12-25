package com.example.chatapp.ui.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.model.ChatRoom
import com.example.chatapp.data.model.Message
import com.example.chatapp.data.model.UserData
import com.example.chatapp.data.repository.CallRepository
import com.example.chatapp.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val callRepository: CallRepository
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
        viewModelScope.launch {
            chatRepository.fetchAndCreateChatRoom(userUID = user.UID!!, partnerUID = partner.UID!!)
                .collect { chatRoom ->
                    chatUiState = chatUiState.copy(chatRoom = chatRoom)
                }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            chatRepository.sendMessage(chatUiState.chatRoom!!.id, message)
        }
    }

    fun fetchStreamUserToken(userId: String): String {
        return runBlocking {
            callRepository.fetchUserToken(userId)
        }
    }

}

data class ChatUiState(
    val user: UserData? = null,
    val partner: UserData? = null,
    val chatRoom: ChatRoom? = null,
)