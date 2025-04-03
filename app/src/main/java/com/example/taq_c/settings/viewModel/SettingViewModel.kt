package com.example.taq_c.settings.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.taq_c.utilities.LocationHelper


class SettingViewModel() : ViewModel() {

    private val currentLocation = LocationHelper.locationState
    fun setLanguage(context: Context,value: String="en"){
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
       sharedPreferences.edit().apply{
           putString("Language",value)
           apply()
       }
    }

    fun setWindSpeedUnit(context: Context,value: String="km/h"){
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("WindSpeedUnit",value)
            apply()
        }
    }

    fun setTemperatureUnit(context: Context , value: String="metric"){
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply{
            putString("TemperatureUnit",value)
            apply()
        }
    }

    fun setLocationType(context: Context,value : String="GPS"){
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("Location",value)
            apply()
        }
        val sharedPref = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            putLong("Latitude",currentLocation.value.latitude.toLong())
            putLong("Longitude",currentLocation.value.longitude.toLong())
            apply()
        }
    }

    fun getLanguage(context: Context): String{
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("Language","en")?:"en"
    }

    fun getTemperatureUnit(context: Context):String{
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TemperatureUnit","metric")?:"metric"
    }

    fun getLocationType(context: Context):String{
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("Location","GPS")?:"GPS"
    }

    fun getWindSpeedUnit(context: Context):String{
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("WindSpeedUnit","km/h")?:"km/h"
    }

    fun setTheSelectedLanguage(context: Context,lang: String){
        val sharedPreferences = context.getSharedPreferences("SelectedLangNo", Context.MODE_PRIVATE)
        when(lang){
            "Arabic"->{
                sharedPreferences.edit().apply {
                    putInt("Code",0)
                    apply()
                }
            }
            "English" ->{
                sharedPreferences.edit().apply {
                    putInt("Code",1)
                    apply()
                }
            }
            "Default" ->{
                sharedPreferences.edit().apply {
                    putInt("Code",2)
                    apply()
                }
            }
        }

    }

    fun getTheSelectedLanguage(context: Context) :Int{
        val sharedPreferences = context.getSharedPreferences("SelectedLangNo", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("Code",1)
    }

    fun setTheSelectedTemperature(context: Context,temp: String){
        val sharedPreferences = context.getSharedPreferences("SelectedTempUnit", Context.MODE_PRIVATE)
        when(temp){
            "Celsius"->{
                sharedPreferences.edit().apply {
                    putInt("Code",0)
                    apply()
                }
            }
            "Kelvin" ->{
                sharedPreferences.edit().apply {
                    putInt("Code",1)
                    apply()
                }
            }
            "Fahrenheit" ->{
                sharedPreferences.edit().apply {
                    putInt("Code",2)
                    apply()
                }
            }
        }
    }

    fun getTheSelectedTemperature(context: Context) :Int{
        val sharedPreferences = context.getSharedPreferences("SelectedTempUnit", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("Code",0)
    }

    fun setTheSelectedSpeedUnit(context: Context,speed: String){
        val sharedPreferences = context.getSharedPreferences("SelectedSpeedUnit", Context.MODE_PRIVATE)
        when(speed){
            "km/h"->{
                sharedPreferences.edit().apply {
                    putInt("Code",0)
                    apply()
                }
            }
            "mph" ->{
                sharedPreferences.edit().apply {
                    putInt("Code",1)
                    apply()
                }
            }
        }
    }
    fun getTheSelectedSpeedUnit(context: Context) :Int{
        val sharedPreferences = context.getSharedPreferences("SelectedSpeedUnit", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("Code",0)
    }

    fun setTheSelectedLocationType(context: Context,type: String){
        val sharedPreferences = context.getSharedPreferences("SelectedLocationType", Context.MODE_PRIVATE)
        when(type){
            "GPS"->{
                sharedPreferences.edit().apply {
                    putInt("Code",0)
                    apply()
                }
            }
            "Map" ->{
                sharedPreferences.edit().apply {
                    putInt("Code",1)
                    apply()
                }
            }
        }
    }

    fun getTheSelectedLocationType(context: Context) :Int{
        val sharedPreferences = context.getSharedPreferences("SelectedLocationType", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("Code",0)
    }
}
