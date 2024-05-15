package ru.guzeevmd.activediabetesassistant.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import ru.guzeevmd.activediabetesassistant.auth.AuthViewModel
import ru.guzeevmd.activediabetesassistant.data.models.LoginUserCommand
import ru.guzeevmd.activediabetesassistant.data.models.RegisterUserCommand


@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    snackbarHostState: SnackbarHostState,
    paddingValues: PaddingValues,
    errorMessage: String? = null) {
    var email by remember { mutableStateOf("") }
    var isEmailValid by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var isPasswordValid by remember { mutableStateOf(false) }
    var isLogin by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (errorMessage != null){
            snackbarHostState.showSnackbar(
                message = errorMessage
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = email,
            onValueChange = { input ->
                email = input
                isEmailValid = input.isNotEmpty() && input.matches(Regex("^[\\w-.]+@([\\w-]+.)+[\\w-]{2,4}$"))},
            label = { Text("Почта") },
            isError = !isEmailValid
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { input ->
                password = input
                isPasswordValid = input.isNotEmpty() && input.matches(Regex("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,}$"))},
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            isError = !isPasswordValid
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.loginUser(LoginUserCommand(email, password))
        }) {
            Text("Войти")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            viewModel.registerUser(RegisterUserCommand(email, password))
        }) {
            Text("Регистрация")
        }
    }
}