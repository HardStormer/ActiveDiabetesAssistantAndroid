package ru.guzeevmd.activediabetesassistant.cards

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import ru.guzeevmd.activediabetesassistant.auth.AuthViewModel
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.DiabetesType
import ru.guzeevmd.activediabetesassistant.data.models.PersonInfoViewModel
import ru.guzeevmd.activediabetesassistant.data.models.Sex
import ru.guzeevmd.activediabetesassistant.data.models.UpdateMyPersonInfoCommand

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PersonInfoCard(
    viewModel: AuthViewModel,
    personInfo: PersonInfoViewModel,
    paddingValues: PaddingValues,
    snackbarHostState: SnackbarHostState,
    authToken: String
) {
    var name by remember { mutableStateOf(personInfo.name ?: "") }
    var age by remember { mutableStateOf(personInfo.age.toString()) }
    var sex by remember { mutableIntStateOf(personInfo.sex) }
    var diabetesType by remember { mutableIntStateOf(personInfo.diabetesType) }
    var isEditable by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = !isEditable,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Возраст") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            readOnly = !isEditable,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditable) {
            var expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { expanded = true }
            ) {
                Text(
                    text = Sex.fromInt(sex).name,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Sex.values().forEach { option ->
                        DropdownMenuItem(text = {
                            Text(text = option.name)
                        }, onClick = {
                            sex = option.value
                            expanded = false
                        })
                    }
                }
            }
        } else {
            TextField(
                value = Sex.fromInt(sex).name,
                onValueChange = {},
                label = { Text("Пол") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditable) {
            var expanded by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { expanded = true }
            ) {
                Text(
                    text = DiabetesType.fromInt(diabetesType).name,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DiabetesType.values().forEach { option ->
                        DropdownMenuItem(text = {
                            Text(text = option.name)
                        }, onClick = {
                            diabetesType = option.value
                            expanded = false
                        })
                    }
                }
            }
        } else {
            TextField(
                value = DiabetesType.fromInt(diabetesType).name,
                onValueChange = {},
                label = { Text("Тип диабета") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Column {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        } else {
            if (isEditable) {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        coroutineScope.launch {
                            isLoading = true
                            val command = UpdateMyPersonInfoCommand(
                                name = name,
                                age = age.toInt(),
                                sex = sex,
                                diabetesType = diabetesType.toInt()
                            )
                            val client = DiabetesAssistantApiClient(authToken)
                            val res = client.updatePersonInfo(command)

                            isLoading = false
                            isEditable = false
                            if (res.status.isSuccess()) {
                                snackbarHostState.showSnackbar("Данные сохранены")
                            } else {
                                snackbarHostState.showSnackbar("${res.status} ${res.bodyAsText()}")
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Сохранить")
                }
            } else {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { isEditable = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Обновить")
                    }


                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { viewModel.logoutUser() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Выйти")
                    }
                }
            }
        }
    }
}
