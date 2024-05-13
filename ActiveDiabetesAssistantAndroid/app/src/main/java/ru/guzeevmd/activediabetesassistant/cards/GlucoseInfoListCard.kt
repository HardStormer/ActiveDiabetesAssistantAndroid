package ru.guzeevmd.activediabetesassistant.cards

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import ru.guzeevmd.activediabetesassistant.data.models.GlucoseInfoViewModel

@Composable
fun GlucoseInfoListCard(glucoseInfoList: Collection<GlucoseInfoViewModel>) {
    LazyColumn{
        items(glucoseInfoList.size) {
            GlucoseInfoCard(glucoseInfoList.elementAt(it))
        }
    }
}