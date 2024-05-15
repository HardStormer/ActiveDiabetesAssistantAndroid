package ru.guzeevmd.activediabetesassistant.auth

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.LoginUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.RegisterUserCommand

class AuthViewModel(private val repository: DiabetesAssistantApiClient, val contextInner: Context) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(
        if (getJwtToken() != null){
            AuthState.Authenticated(getJwtToken().toString())
        }
        else {
            AuthState.Idle
        })
    val authState: State<AuthState> = _authState
    private val _context = mutableStateOf<Context>(contextInner)

    // Save JWT Token to SharedPreferences
    private fun saveJwtToken(token: String) {
        val sharedPreferences = contextInner.getSharedPreferences("myPrefs2", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        editor.apply()
    }

    // Clear JWT Token from SharedPreferences
    private fun clearJwtToken() {
        val sharedPreferences = contextInner.getSharedPreferences("myPrefs2", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("jwt_token")
        editor.apply()
    }

    // Retrieve JWT Token from SharedPreferences
    fun getJwtToken(): String? {
        val sharedPreferences = contextInner.getSharedPreferences("myPrefs2", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", null)
    }

    fun registerUser(credentials: RegisterUserCommand) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val response = repository.registerUser(credentials)
            if (response?.token != null) {
                _authState.value = AuthState.Authenticated(response.token)
                saveJwtToken(response.token) // Save the token
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
                saveJwtToken(response.token) // Save the token
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
        clearJwtToken() // Clear the token
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Authenticated(val token: String) : AuthState()
    data class Error(val message: String) : AuthState()
}