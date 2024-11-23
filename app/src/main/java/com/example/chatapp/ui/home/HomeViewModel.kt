package com.example.chatapp.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.model.UserData
import com.example.chatapp.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {
    var homeUiState: HomeUiState by mutableStateOf(HomeUiState())
        private set

    init {
        getAllUsers()
    }

    fun getAllUsers() {
        viewModelScope.launch {
            homeRepository.getAllUsers()
                .catch { e ->
                    println("Error: $e")
                }
                .collect { users ->
                    homeUiState = HomeUiState(userDataList = users)
                }
        }
    }

    fun findUsersByUsername(query: String) {
        viewModelScope.launch {
            homeRepository.findUsersByUsername(query)
                .catch { e ->
                    println("Error: $e")
                }
                .collect { users ->
                    homeUiState = HomeUiState(userDataList = users)
                }
        }
    }
}

data class HomeUiState(
    val userDataList: List<UserData> = listOf()
)
