package ru.guzeevmd.activediabetesassistant.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.guzeevmd.activediabetesassistant.data.models.PersonInfoViewModel

@Composable
fun PersonInfoCard(personInfo: PersonInfoViewModel) {
    Column {
        Text(personInfo.name.toString())
        Text(personInfo.age.toString())
        Text(personInfo.sex.toString())
        Text(personInfo.diabetesType.toString())
    }
}