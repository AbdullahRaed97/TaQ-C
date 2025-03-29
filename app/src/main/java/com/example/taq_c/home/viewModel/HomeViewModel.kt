package com.example.taq_c.home.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taq_c.utilities.LocationHelper
import com.example.taq_c.data.model.Forecast
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val weatherResponse_ : MutableStateFlow<Response<WeatherResponse>> =
        MutableStateFlow(Response.Loading)
    val weatherResponse=
        weatherResponse_.asStateFlow()
    private val forecastResponse_ : MutableStateFlow<Response<ForecastResponse>> =
        MutableStateFlow(Response.Loading)
    val forecastResponse =
        forecastResponse_.asStateFlow()
    private val message_ : MutableSharedFlow<String> =
        MutableSharedFlow()
    val message  =
        message_.asSharedFlow()

    val currentLocation = LocationHelper.locationState

    fun getCurrentWeatherData(lat: Double,lon: Double,units: String , lang: String){
        viewModelScope.launch {
            try{
               weatherRepository.getCurrentWeatherData(lat = lat, lon = lon, units = units , lang = lang )
                   .catch {
                       weatherResponse_.emit(Response.Failure(it))
                       message_.emit(it.message.toString())
                   }.collect {
                       weatherResponse_.emit(Response.Success<WeatherResponse>(it as WeatherResponse))
                   }
            }catch (e:Exception){
                weatherResponse_.emit(Response.Failure(e))
                message_.emit(e.message.toString())
            }
        }
    }

    fun get5D_3HForecastData(lat: Double,lon: Double,units: String,lang: String){
        viewModelScope.launch {
            try{
                weatherRepository.get5D_3HForeCastData(lat = lat , lon = lon , units = units, lang = lang)
                    .catch {
                        forecastResponse_.emit(Response.Failure(it))
                        message_.emit(it.message.toString())
                    }.collect {
                        forecastResponse_.emit(Response<ForecastResponse>.Success(it) as Response<ForecastResponse>)
                    }
            }catch (e:Exception){
                message_.emit(e.message.toString())
                forecastResponse_.emit(Response.Failure(e))
            }
        }
    }

    fun getAppUnit(context: Context): String{
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TemperatureUnit","metric")?:"metric"
    }

    fun getAppLanguage(context: Context): String{
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("Language","en")?:"en"
    }

    fun getAppWindSpeedUnit(context: Context): String{
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("WindSpeedUnit","km/h")?:"km/h"
    }

    fun calculateWindSpeed(speedUnit : String , weatherResponse: WeatherResponse): String{
        var speed = ""
        when(speedUnit){
            "km/h" ->{
                weatherResponse.wind?.let {
                    speed = (it.windSpeed*1.609).toString()
                }
            }
            "mph" ->{
                weatherResponse.wind?.let {
                    speed = (it.windSpeed*0.621).toString()
                }
            }
        }
        return speed
    }

    fun getAppLatitude(lat: Double,context: Context) : Double{
        var latitude = lat
        val locationType = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .getString("Location","GPS")
        when(locationType){
            "Map" -> {
                val sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
                latitude = sharedPreferences.getLong("Latitude",0).toDouble()
            }
            "GPS" -> latitude = lat
        }
        return latitude
    }

    fun getAppLongitude(lon: Double,context: Context) : Double{
        var longitude = lon
        val locationType = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .getString("Location","GPS")
        when(locationType){
            "Map" -> {
                val sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
                longitude = sharedPreferences.getLong("Longitude",0).toDouble()
            }
            "GPS" -> longitude = lon
        }
        return longitude
    }

    fun setLatitude(context: Context , lat : Double){
        val sharedPreferences =context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putLong("Latitude",lat.toLong())
            apply()
        }
    }

    fun setLongitude(context: Context, lon: Double){
        val sharedPreferences =context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putLong("Longitude",lon.toLong())
            apply()
        }
    }

    fun filterForecastList(forecastList:List<Forecast>):List<Forecast>{
        val finalList = mutableListOf<Forecast>()
        val datesFound = mutableSetOf<String>()
        for (forecast in forecastList) {
            val date = forecast.dt_txt?.substringBefore(" ") ?: continue
            val time = forecast.dt_txt?.substringAfter(" ") ?: continue
            if (time.startsWith("12:00") && !datesFound.contains(date)) {
                finalList.add(forecast)
                datesFound.add(date)
                if (finalList.size >= 5) {
                    break
                }
            }
        }
        Log.i("TAG", "filterForecastList: $finalList")
        return finalList
    }

    fun convertTimeStampToTime(timeStamp: Long): String {
        val date = Date(timeStamp * 1000L)
        val sdf = SimpleDateFormat("h:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    fun convertTimeStampToDate(timeStamp: Long): String {
        val date = Date(timeStamp * 1000L)
        val sdf = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun getCountryName(countryCode: String?): String {
        return if (!countryCode.isNullOrBlank()) {
            try {
                Locale("", countryCode).displayCountry ?: "Unspecified"
            } catch (e: Exception) {
                Log.i("TAG", "getCountryName: error is ${e.message} ")
                "Unspecified"
            }
        } else {
            "Unspecified"
        }
    }

    fun getUnit(units: String): String {
        when (units) {
            "metric" -> {
                return "°C"
            }

            "kelvin" -> {
                return "K"
            }

            "imperial" -> {
                return "°f"
            }

            else -> {
                return "°C"
            }
        }
    }

    fun getDayName(dt: Long): String {
        val date = Date(dt * 1000L) // Convert seconds to milliseconds
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        Log.i("TAG", "getDayName: ${sdf.format(date)} , dt : $dt , date : $date")
        return sdf.format(date)
    }

}

class HomeFactory(private val repository: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}