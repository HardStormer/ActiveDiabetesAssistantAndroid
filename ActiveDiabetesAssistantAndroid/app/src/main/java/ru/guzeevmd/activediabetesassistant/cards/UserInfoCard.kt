package ru.guzeevmd.activediabetesassistant.cards

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import ru.guzeevmd.activediabetesassistant.data.models.UserViewModel

@Composable
fun UserInfoCard(user: UserViewModel) {
    Column {
        Text(user.email.toString())
    }
}