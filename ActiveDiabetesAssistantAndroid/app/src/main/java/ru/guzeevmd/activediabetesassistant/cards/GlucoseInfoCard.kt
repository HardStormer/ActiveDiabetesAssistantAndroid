package ru.guzeevmd.activediabetesassistant.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel

@Composable
fun GlucoseInfoCard(glucoseInfo: GlucoseInfoViewModel) {
    Column {
        Text(glucoseInfo.glucoseData.toString())
        Text(glucoseInfo.stepsCount.toString())
    }
}