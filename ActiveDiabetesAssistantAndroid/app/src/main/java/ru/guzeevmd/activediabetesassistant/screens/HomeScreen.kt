package ru.guzeevmd.activediabetesassistant.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.guzeevmd.activediabetesassistant.cards.TimingDiagram
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel
import ru.guzeevmd.activediabetesassistant.ui.theme.NavigationBarMediumTheme

@Composable
fun HomeScreen(navController: NavController, authToken: String) {
    val glucoseInfoSet = remember {
        mutableStateOf(setOf<GlucoseInfoViewModel>())
    }
    val exSet = remember {
        mutableStateOf(setOf<Exception>())
    }

    var isDataGetted by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    var labelCount by remember { mutableStateOf(5) }

    NavigationBarMediumTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LaunchedEffect(refreshTrigger) {
                    isDataGetted = false
                    val client = DiabetesAssistantApiClient(authToken)
                    var resp: Collection<GlucoseInfoViewModel> = emptyList()
                    try {
                        resp = client.getGlucoseInfoCollection(10, 0).modelList
                        glucoseInfoSet.value = resp.toSet()
                        isDataGetted = true
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(
                            message = e.message.toString()
                        )
                    }
                }

                if (isDataGetted) {
                    if (glucoseInfoSet.value.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Нет записей")
                        }
                    } else {
                        TimingDiagram(glucoseInfoList = glucoseInfoSet.value.toList().asReversed(), labelCount)

                        Text("Выберите количество делений", style = MaterialTheme.typography.titleMedium)
                        Slider(
                            value = labelCount.toFloat(),
                            onValueChange = { labelCount = it.toInt() },
                            valueRange = 2f..10f,
                            steps = 8,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(horizontal = 15.dp, vertical = 10.dp)
                                .clip(MaterialTheme.shapes.large)
                        ) {
                            Text(text = "Риск либо возможен, либо нет")
                        }
                    }
                }
            }
        }
    }
}