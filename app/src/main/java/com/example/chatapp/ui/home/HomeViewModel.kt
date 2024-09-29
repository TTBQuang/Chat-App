package com.example.chatapp.ui.home

import androidx.lifecycle.ViewModel
import com.example.chatapp.data.network.UserData
import com.example.chatapp.data.repository.SignInRepository
import com.example.chatapp.ui.login.SignInState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val signInRepository: SignInRepository
) : ViewModel() {
    //private val _state = MutableStateFlow(HomeState())
    //val state = _state.asStateFlow()
}

data class HomeState(
    val userData: UserData
)