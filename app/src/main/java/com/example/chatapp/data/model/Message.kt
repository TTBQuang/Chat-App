package com.example.chatapp.data.model

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val content: String = "",
    val sender: String = "",
    val sentAt: Timestamp? = null
)