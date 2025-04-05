package com.example.taq_c.settings.viewModel

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.example.taq_c.utilities.LocationHelper


class SettingViewModel() : ViewModel() {

    private val currentLocation = LocationHelper.locationState
    fun setLanguage(context: Context, value: String = "en") {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("Language", value)
            apply()
        }
    }

    fun setWindSpeedUnit(context: Context, value: String = "km/h") {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("WindSpeedUnit", value)
            apply()
        }
    }

    fun setTemperatureUnit(context: Context, value: String = "metric") {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("TemperatureUnit", value)
            apply()
        }
    }

    fun setLocationType(context: Context, value: String = "GPS") {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("Location", value)
            apply()
        }
        val sharedPref = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            putFloat("Latitude", currentLocation.value.latitude.toFloat())
            putFloat("Longitude", currentLocation.value.longitude.toFloat())
            apply()
        }
    }

    fun getSelectedLanguagePreference(context: Context): Int {
        val code = context.getSharedPreferences("Settings", 0).getString("Language", "")

        return when (code) {
            "ar" -> 0
            "en" -> 1
            else -> 2
        }
    }

    fun getSelectedTempPreference(context: Context): Int {
        val code = context.getSharedPreferences("Settings", 0).getString("TemperatureUnit", "")
        return when (code) {
            "metric" -> 0
            "kelvin" -> 1
            "imperial" -> 2
            else -> 0
        }
    }

    fun getSelectedWindSpeedUnitPreference(context: Context): Int {
        val code = context.getSharedPreferences("Settings", 0).getString("WindSpeedUnit", "")
        return when (code) {
            "km/h" -> 0
            "mph" -> 1
            else -> 0
        }
    }

    fun getSelectedLocationPreference(context: Context): Int {
        val code = context.getSharedPreferences("Settings", 0).getString("Location", "")
        return when (code) {
            "GPS" -> 0
            "Map" -> 1
            else -> 0
        }
    }

    fun setAppLanguage(context: Context, languageCode: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.getSystemService(LocaleManager::class.java)
                .applicationLocales = LocaleList.forLanguageTags(languageCode)
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageCode))
        }
    }
}
