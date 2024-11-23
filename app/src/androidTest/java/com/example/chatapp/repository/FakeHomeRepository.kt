package com.example.chatapp.repository

import com.example.chatapp.data.model.UserData
import com.example.chatapp.data.repository.HomeRepository
import com.example.chatapp.helper.fakeSearchUserDataList
import com.example.chatapp.helper.fakeUserDataList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeHomeRepository @Inject constructor(
) : HomeRepository {
    override fun getAllUsers(): Flow<List<UserData>> {
        return flow {
            delay(10)
            emit(fakeUserDataList)
        }
    }

    override fun findUsersByUsername(query: String): Flow<List<UserData>> {
        return flow {
            delay(10)
            emit(fakeSearchUserDataList)
        }
    }
}
