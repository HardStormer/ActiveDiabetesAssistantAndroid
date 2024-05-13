package ru.guzeevmd.activediabetesassistant.activities

sealed class Screens(val route : String) {
    object Home : Screens("home_screen")
    object Search : Screens("search_screen")
    object Profile : Screens("profile_screen")
}