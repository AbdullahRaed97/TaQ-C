package com.example.taq_c.utilities

import kotlinx.serialization.Serializable

@Serializable
sealed class NavigationRoute {
    @Serializable
    object HomeScreen : NavigationRoute()
    @Serializable
    object FavoriteScreen : NavigationRoute()
    @Serializable
    object SettingScreen : NavigationRoute()
    @Serializable
    object AlarmScreen : NavigationRoute()
    @Serializable
    object MapScreen : NavigationRoute()
}