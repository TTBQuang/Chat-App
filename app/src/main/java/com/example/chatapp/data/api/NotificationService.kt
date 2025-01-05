package com.example.chatapp.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class NotificationRequest(
    val token: String,
    val title: String,
    val body: String
)

interface NotificationService {
    @POST("sendNotification")
    fun sendNotification(@Body request: NotificationRequest): Call<Void>
}