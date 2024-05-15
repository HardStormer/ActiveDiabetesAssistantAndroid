package ru.guzeevmd.activediabetesassistant.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import ru.guzeevmd.activediabetesassistant.auth.AuthScreen
import ru.guzeevmd.activediabetesassistant.auth.AuthViewModel
import ru.guzeevmd.activediabetesassistant.data.client.DiabetesAssistantApiClient
import ru.guzeevmd.activediabetesassistant.ui.theme.NavigationBarMediumTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val client = DiabetesAssistantApiClient(null)

        val viewModel = AuthViewModel(client, applicationContext)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AuthScreen(viewModel = viewModel)
        }
    }

    @Composable
    fun Greeting(name: String) {
        Text(text = "Hello $name!")
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        NavigationBarMediumTheme {
            Greeting("Android")
        }
    }
}