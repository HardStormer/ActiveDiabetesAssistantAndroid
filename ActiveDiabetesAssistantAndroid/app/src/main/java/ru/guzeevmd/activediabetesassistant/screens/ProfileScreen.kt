package ru.guzeevmd.activediabetesassistant.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import ru.guzeevmd.activediabetesassistant.cards.PersonInfoCard
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.PersonInfoViewModel
import ru.guzeevmd.activediabetesassistant.ui.theme.NavigationBarMediumTheme

@Composable
fun ProfileScreen(navController: NavController) {
    val personInfoSet = remember {
        mutableStateOf<PersonInfoViewModel?>(null)
    }
    NavigationBarMediumTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val scope = rememberCoroutineScope()

            LaunchedEffect(scope) {
                scope.launch {

                }
                val client = DiabetesAssistantApiClient()
                client.getPersonInfo()?.also { personInfoSet.value = it }
            }

            personInfoSet.value?.let { PersonInfoCard(personInfo = it) }

//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(15.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            )  {
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .padding(horizontal = 15.dp, vertical = 10.dp)
//                        .clip(MaterialTheme.shapes.large)
//                ) {
//                }
//                Text(
//                    "Profile Screen",
//                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.padding(vertical = 20.dp)
//                )
//            }
        }
    }
}