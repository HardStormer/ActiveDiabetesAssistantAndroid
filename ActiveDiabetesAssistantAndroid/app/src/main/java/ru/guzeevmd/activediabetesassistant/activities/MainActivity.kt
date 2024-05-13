package ru.guzeevmd.activediabetesassistant.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.composable
import ru.guzeevmd.activediabetesassistant.GlucoseLevelsActivity
import ru.guzeevmd.activediabetesassistant.UserProfileActivity
import ru.guzeevmd.activediabetesassistant.WorkoutTimeActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ModalNavigationDrawer(
                    drawerContent = {
                        DrawerContent(this@MainActivity)
                    }
                ) {
                    // Main content
                }
            }
        }
    }
}

//@Composable
//fun MainContent() {
//    val navController = rememberNavController()
//    val drawerState = rememberDrawerState(DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//
//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            DrawerContent(onDestinationClicked = { route ->
//                scope.launch {
//                    drawerState.close()
//                    navController.navigate(route)
//                }
//            })
//        }
//    ) {
//        NavigationGraph(navController = navController)
//    }
//}

@Composable
fun DrawerContent(activity: ComponentActivity) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        DrawerButton("Home", onClick = {
            // Navigate inside the app or refresh the home
        })
        DrawerButton("Glucose Levels", onClick = {
            activity.startActivity(Intent(activity, GlucoseLevelsActivity::class.java))
        })
        DrawerButton("User Profile", onClick = {
            activity.startActivity(Intent(activity, UserProfileActivity::class.java))
        })
        DrawerButton("Workout Time", onClick = {
            activity.startActivity(Intent(activity, WorkoutTimeActivity::class.java))
        })
    }
}

@Composable
fun DrawerButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Text(text)
    }
}

@Composable
fun NavigationGraph(navController: androidx.navigation.NavHostController) {
    androidx.navigation.compose.NavHost(navController, startDestination = "home") {
        composable("home") { Text("Home Screen") }
        composable("profile") { Text("Profile Screen") }
        composable("settings") { Text("Settings Screen") }
    }
}