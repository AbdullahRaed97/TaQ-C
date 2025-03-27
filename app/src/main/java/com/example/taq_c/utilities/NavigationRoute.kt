package com.example.taq_c.utilities

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoute {
    @Serializable
    data class HomeScreen(val lat: Double,val lon: Double,val units: String) : NavigationRoute()
    @Serializable
    object FavoriteScreen : NavigationRoute()
    @Serializable
    object SettingScreen : NavigationRoute()
    @Serializable
    object AlarmScreen : NavigationRoute()
    @Serializable
    object MapScreen : NavigationRoute()
}