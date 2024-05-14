package ru.guzeevmd.activediabetesassistant.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.CreateGlucoseInfoCommand

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GlucoseInfoCreateCard(
    snackbarHostState: SnackbarHostState,
    onClose: () -> Unit) {
    var glucoseLevel by remember { mutableStateOf("") }
    var stepsCount by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Close")
                }
            }
            TextField(
                value = glucoseLevel,
                onValueChange = { glucoseLevel = it },
                label = { Text("Уровень глюкозы") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = stepsCount,
                onValueChange = { stepsCount = it },
                label = { Text("Количество шагов за день") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                CircularProgressIndicator()
            }

            Button(
                onClick = {
                    keyboardController?.hide()
                    coroutineScope.launch {
                        isLoading = true
                        // Handle save action
                        val command = CreateGlucoseInfoCommand(
                            glucoseData = glucoseLevel.toInt(),
                            stepsCount = stepsCount.toInt()
                        )
                        val client = DiabetesAssistantApiClient()
                        val res = client.createGlucoseInfo(command)

                        isLoading = false
                        onClose()
                        if (res != null) {
                            snackbarHostState.showSnackbar(
                                message = "Данные сохранены"
                            )
                        } else {
                            snackbarHostState.showSnackbar(
                                message = "Произошла ошибка"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сохранить")
            }
        }

    }
}