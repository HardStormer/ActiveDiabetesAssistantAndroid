package ru.guzeevmd.activediabetesassistant.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.data.models.DeleteGlucoseInfoCommand
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun GlucoseInfoCard(
    glucoseInfo: GlucoseInfoViewModel,
    pagingValues: PaddingValues,
    snackbarHostState: SnackbarHostState,
    onDelete: () -> Unit) {
    val date = ZonedDateTime.parse(glucoseInfo.createdAt, DateTimeFormatter.ISO_ZONED_DATE_TIME).toLocalDateTime()

    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm", Locale.getDefault())),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(Modifier.weight(1f))
                Button(onClick = {
                    coroutineScope.launch {
                        val client = DiabetesAssistantApiClient()
                        val command = DeleteGlucoseInfoCommand(
                            false,
                            glucoseInfo.id
                        )
                        val res = client.deleteGlucoseInfo(command)
                        onDelete()
                        if (res.status.isSuccess()) {
                            snackbarHostState.showSnackbar(
                                message = "Данные удалены"
                            )
                        } else {
                            snackbarHostState.showSnackbar(
                                message = "Произошла ошибка"
                            )
                        }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Glucose Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${glucoseInfo.glucoseData} mg/dL",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Steps Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = glucoseInfo.stepsCount?.toString() ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}