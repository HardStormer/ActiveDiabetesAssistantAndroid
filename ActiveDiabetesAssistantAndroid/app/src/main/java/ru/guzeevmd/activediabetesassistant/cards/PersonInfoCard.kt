package ru.guzeevmd.activediabetesassistant.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.guzeevmd.activediabetesassistant.data.models.DiabetesType
import ru.guzeevmd.activediabetesassistant.data.models.PersonInfoViewModel
import ru.guzeevmd.activediabetesassistant.data.models.Sex

@Composable
fun PersonInfoCard(personInfo: PersonInfoViewModel) {
    Row{
        Column {
            Text("Имя")
            Text("Возраст")
            Text("Пол")
            Text("Тип диабета")
        }
        Column {
            Text(personInfo.name.toString())
            Text(personInfo.age.toString())
            Text(Sex.entries[personInfo.sex].toString())
            Text(DiabetesType.entries[personInfo.diabetesType].toString())
        }
    }
}