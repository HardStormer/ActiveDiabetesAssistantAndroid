package ru.guzeevmd.activediabetesassistant.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
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
    NavigationBarMediumTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LaunchedEffect(Unit) {
                val client = DiabetesAssistantApiClient()
                var resp : PersonInfoViewModel? = null
                try {
                    resp = client.getPersonInfo()
                } catch (e: Exception) {
                    println("${e.message}")
                }
                resp?.also { personInfoSet.value = resp }
            }

            personInfoSet.value?.let { PersonInfoCard(personInfo = it) }

            for (ex in exSet) {
                Text(ex.message.toString())
            }
        }
    }
}