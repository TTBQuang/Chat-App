package com.example.chatapp.data.repository

import com.example.chatapp.data.api.RetrofitInstance
import com.example.chatapp.data.model.UserIdRequest
import jakarta.inject.Inject

interface CallRepository {
    suspend fun fetchUserToken(userId: String): String
}

class CallRepositoryImpl @Inject constructor() : CallRepository {
    private val apiService = RetrofitInstance.apiService

    override suspend fun fetchUserToken(userId: String): String {
        try {
            val userIdRequest = UserIdRequest(userId)
            val response = apiService.fetchUserToken(userIdRequest)
            return response.userToken
        } catch (e: Exception) {
            throw Exception("Error fetching user token: ${e.message}")
        }
    }
}
