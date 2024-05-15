package ru.guzeevmd.activediabetesassistant.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.guzeevmd.activediabetesassistant.activities.BottomNavigationBar
import ru.guzeevmd.activediabetesassistant.auth.AuthState.Authenticated
import ru.guzeevmd.activediabetesassistant.screens.LoginScreen
import ru.guzeevmd.activediabetesassistant.ui.theme.NavigationBarMediumTheme

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val authState by remember { viewModel.authState }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            when (authState) {
                is AuthState.Idle -> {
                    LoginScreen(viewModel, snackbarHostState, paddingValues)
                }
                is AuthState.Loading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
                        CircularProgressIndicator(
                            modifier = Modifier.padding(vertical = 20.dp)
                        )
                    }
                }
                is Authenticated -> {
                    NavigationBarMediumTheme {
                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            BottomNavigationBar(viewModel, (authState as Authenticated).token)
                        }
                    }
                }
                is AuthState.Error -> {
                    val errorMessage = (authState as AuthState.Error).message
                    LoginScreen(viewModel, snackbarHostState, paddingValues, errorMessage = errorMessage)
                }
            }
        })

}
