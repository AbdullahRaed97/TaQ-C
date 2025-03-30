package com.example.taq_c.utilities

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoute {
    @Serializable
    data class HomeScreen(val lat: Double,val lon: Double) : NavigationRoute()
    @Serializable
    object FavoriteScreen : NavigationRoute()
    @Serializable
    object SettingScreen : NavigationRoute()
    @Serializable
    object AlertScreen : NavigationRoute()
    @Serializable
    data class MapScreen(val fromSetting: Boolean , val fromAlert: Boolean) : NavigationRoute()
    @Serializable
    data class SetAlertScreen(val lat: Double, val lon: Double) : NavigationRoute()
}