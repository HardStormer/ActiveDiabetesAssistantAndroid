package ru.guzeevmd.activediabetesassistant.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.LoginUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.RegisterUserCommand

class AuthViewModel(private val repository: DiabetesAssistantApiClient) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Idle)
    val authState: State<AuthState> = _authState

    fun registerUser(credentials: RegisterUserCommand) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val response = repository.registerUser(credentials)
            if (response?.token != null) {
                _authState.value = AuthState.Authenticated(response.token)
            } else {
                _authState.value = AuthState.Error("Registration failed")
            }
        }
    }

    fun loginUser(credentials: LoginUserCommand) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val response = repository.loginUser(credentials)
            if (response?.token != null) {
                _authState.value = AuthState.Authenticated(response.token)
            } else {
                _authState.value = AuthState.Error("Login failed")
            }
        }
    }

    fun logoutUser() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.logoutUser()
            _authState.value = AuthState.Idle
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}