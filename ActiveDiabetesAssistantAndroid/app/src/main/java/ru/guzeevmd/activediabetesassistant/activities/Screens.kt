package ru.guzeevmd.activediabetesassistant.activities

sealed class Screens(val route : String) {
    object Home : Screens("home_screen")
    object GlucoseLevels : Screens("glucose_levels_screen")
    object Profile : Screens("profile_screen")
}