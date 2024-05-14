package ru.guzeevmd.activediabetesassistant.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import ru.guzeevmd.activediabetesassistant.cards.PersonInfoCard
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.PersonInfoViewModel
import ru.guzeevmd.activediabetesassistant.ui.theme.NavigationBarMediumTheme

@Composable
fun ProfileScreen(navController: NavController) {
    val personInfoSet = remember {
        mutableStateOf<PersonInfoViewModel?>(null)
    }
    val exSet = remember {
        mutableSetOf<Exception>()
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            NavigationBarMediumTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LaunchedEffect(Unit) {
                        val client = DiabetesAssistantApiClient()
                        var resp: PersonInfoViewModel? = null
                        try {
                            resp = client.getPersonInfo()
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(
                                message = e.message.toString()
                            )
                        }
                        resp?.also { personInfoSet.value = resp }
                    }

                    personInfoSet.value?.let { PersonInfoCard(personInfo = it, paddingValues, snackbarHostState) }

                    for (ex in exSet) {
                        Text(ex.message.toString())
                    }
                }
            }
        }
    )
}