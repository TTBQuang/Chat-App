package com.example.chatapp.data.model

data class ChatRoom (
    val id: String = "",
    val messages: List<Message> = listOf()
)