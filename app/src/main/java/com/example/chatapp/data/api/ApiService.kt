package com.example.chatapp.data.api

import com.example.chatapp.data.model.StreamUserToken
import com.example.chatapp.data.model.UserIdRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("getUserToken")
    suspend fun fetchUserToken(@Body userIdRequest: UserIdRequest): StreamUserToken
}

