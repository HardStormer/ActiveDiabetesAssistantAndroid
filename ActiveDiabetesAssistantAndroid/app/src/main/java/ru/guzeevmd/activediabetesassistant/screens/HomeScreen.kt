package ru.guzeevmd.activediabetesassistant.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.guzeevmd.activediabetesassistant.cards.TimingDiagram
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel
import ru.guzeevmd.activediabetesassistant.ui.theme.NavigationBarMediumTheme

@Composable
fun HomeScreen(navController: NavController, authToken: String) {
    val glucoseInfoSet = remember { mutableStateOf(setOf<GlucoseInfoViewModel>()) }
    val exSet = remember { mutableStateOf(setOf<Exception>()) }
    var isDataGetted by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var labelCount by remember { mutableStateOf(5) }
    var risk by remember { mutableStateOf("Не определено") }
    var riskExplanation by remember { mutableStateOf("") }
    var riskRecommendation by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    NavigationBarMediumTheme {
        Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(8.dp), // уменьшение общего отступа
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
                        val riskResult = calculateRisk(glucoseInfoSet.value)
                        risk = riskResult.first
                        riskExplanation = riskResult.second
                        riskRecommendation = riskResult.third
                    } catch (e: Exception) {
                        snackbarHostState.showSnackbar(message = e.message.toString())
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
                            Text("Нет записей", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TimingDiagram(glucoseInfoList = glucoseInfoSet.value.toList().asReversed(), labelCount)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Выберите количество делений", style = MaterialTheme.typography.titleMedium)
                            Slider(
                                value = labelCount.toFloat(),
                                onValueChange = { labelCount = it.toInt() },
                                valueRange = 2f..10f,
                                steps = 8,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(text = "Риск: $risk", style = MaterialTheme.typography.titleLarge)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = riskExplanation, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = "Рекомендации:", style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = riskRecommendation, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun calculateRisk(glucoseInfoSet: Set<GlucoseInfoViewModel>): Triple<String, String, String> {
    if (glucoseInfoSet.isEmpty()) return Triple("Недостаточно данных", "", "")

    val glucoseLevels = glucoseInfoSet.map { it.glucoseData }
    val averageGlucose = glucoseLevels.average()
    val outOfRangeCount = glucoseLevels.count { it < 70 || it > 180 }
    val outOfRangePercentage = outOfRangeCount.toDouble() / glucoseLevels.size * 100

    val risk: String
    val explanation: String
    val recommendation: String

    when {
        averageGlucose > 180 || outOfRangePercentage > 20 -> {
            risk = "Высокий"
            explanation = "Средний уровень глюкозы за последнее время составляет ${"%.1f".format(averageGlucose)} мг/дл, и ${"%.1f".format(outOfRangePercentage)}% измерений выходят за пределы нормального диапазона (70-180 мг/дл)."
            recommendation = "Пожалуйста, обратитесь к врачу для коррекции лечения. Возможно, потребуется изменить дозировку инсулина или другие аспекты вашего плана лечения. Регулярно проверяйте уровень глюкозы и следите за диетой."
        }
        averageGlucose in 140.0..180.0 || outOfRangePercentage in 10.0..20.0 -> {
            risk = "Средний"
            explanation = "Средний уровень глюкозы за последнее время составляет ${"%.1f".format(averageGlucose)} мг/дл, и ${"%.1f".format(outOfRangePercentage)}% измерений выходят за пределы нормального диапазона (70-180 мг/дл)."
            recommendation = "Продолжайте следить за своим уровнем глюкозы и поддерживайте здоровый образ жизни. Возможно, стоит обсудить с врачом текущий план лечения и внести небольшие коррективы."
        }
        else -> {
            risk = "Низкий"
            explanation = "Средний уровень глюкозы за последнее время составляет ${"%.1f".format(averageGlucose)} мг/дл, и только ${"%.1f".format(outOfRangePercentage)}% измерений выходят за пределы нормального диапазона (70-180 мг/дл)."
            recommendation = "Ваш уровень глюкозы в норме. Продолжайте поддерживать текущий план лечения и здоровый образ жизни."
        }
    }

    return Triple(risk, explanation, recommendation)
}