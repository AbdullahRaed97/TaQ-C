package com.example.taq_c.settings.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingViewModel(context: Context) : ViewModel() {
    private val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)

    fun setLanguage(value: String="en"){
       sharedPreferences.edit().apply{
           putString("Language",value)
           apply()
       }
    }

    fun setWindSpeedUnit(value: String="km/h"){
        sharedPreferences.edit().apply {
            putString("WindSpeedUnit",value)
            apply()
        }
    }

    fun setTemperatureUnit(value: String="metric"){
        sharedPreferences.edit().apply{
            putString("TemperatureUnit",value)
            apply()
        }
    }

    fun setLocationType(value : String="GPS"){
        sharedPreferences.edit().apply {
            putString("Location",value)
            apply()
        }
    }

    fun getLanguage(): String{
        return sharedPreferences.getString("Language","en")?:"en"
    }

    fun getTemperatureUnit():String{
        return sharedPreferences.getString("TemperatureUnit","metric")?:"metric"
    }

    fun getLocationType():String{
        return sharedPreferences.getString("Location","GPS")?:"GPS"
    }

    fun getWindSpeedUnit():String{
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

class SettingFactory (val context: Context) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(context) as T
    }
}