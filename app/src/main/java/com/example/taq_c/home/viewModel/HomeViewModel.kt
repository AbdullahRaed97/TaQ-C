package com.example.taq_c.home.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taq_c.R
import com.example.taq_c.data.model.Forecast
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.utilities.LocationHelper
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
    private val weatherResponse_: MutableStateFlow<Response<WeatherResponse>> =
        MutableStateFlow(Response.Loading)
    val weatherResponse =
        weatherResponse_.asStateFlow()
    private val forecastResponse_: MutableStateFlow<Response<ForecastResponse>> =
        MutableStateFlow(Response.Loading)
    val forecastResponse =
        forecastResponse_.asStateFlow()
    private val message_: MutableSharedFlow<String> =
        MutableSharedFlow()
    val message =
        message_.asSharedFlow()

    val currentLocation = LocationHelper.locationState

    fun getCurrentWeatherData(lat: Double, lon: Double, units: String, lang: String) {
        viewModelScope.launch {
            try {
                weatherRepository.getCurrentWeatherData(
                    lat = lat,
                    lon = lon,
                    units = units,
                    lang = lang
                )
                    .catch {
                        weatherResponse_.emit(Response.Failure(it))
                        message_.emit(it.message.toString())
                    }.collect {
                        weatherResponse_.emit(Response.Success<WeatherResponse>(it as WeatherResponse))
                    }
            } catch (e: Exception) {
                weatherResponse_.emit(Response.Failure(e))
                message_.emit(e.message.toString())
            }
        }
    }

    fun get5D_3HForecastData(lat: Double, lon: Double, units: String, lang: String) {
        viewModelScope.launch {
            try {
                weatherRepository.get5D_3HForeCastData(
                    lat = lat,
                    lon = lon,
                    units = units,
                    lang = lang
                )
                    .catch {
                        forecastResponse_.emit(Response.Failure(it))
                        message_.emit(it.message.toString())
                    }.collect {
                        forecastResponse_.emit(Response<ForecastResponse>.Success(it) as Response<ForecastResponse>)
                    }
            } catch (e: Exception) {
                message_.emit(e.message.toString())
                forecastResponse_.emit(Response.Failure(e))
            }
        }
    }

    fun getAppUnit(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("TemperatureUnit", "metric") ?: "metric"
    }

    fun getAppLanguage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        return sharedPreferences.getString("Language", "en") ?: "en"
    }

    fun getAppWindSpeedUnit(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langCode = sharedPreferences.getString("Language", "en") ?: "en"
        val unitCode = sharedPreferences.getString("WindSpeedUnit", "km/h") ?: "km/h"
        return when (langCode) {
            "en" -> {
                when (unitCode) {
                    "km/h" -> "km/h"
                    "mph" -> "mph"
                    else -> "km/h"
                }
            }

            "ar" -> {
                when (unitCode) {
                    "km/h" -> "كم/س"
                    "mph" -> "ميل/س"
                    else -> "كم/س"
                }
            }

            else -> {
                when (unitCode) {
                    "km/h" -> "km/h"
                    "mph" -> "mph"
                    else -> "km/h"
                }
            }
        }
    }

    fun calculateWindSpeed(speedUnit: String, weatherResponse: WeatherResponse,context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val langCode = sharedPreferences.getString("Language", "en") ?: "en"
       return when(langCode) {
           "en" -> {
               when (speedUnit) {
                   "km/h" -> {
                       weatherResponse.wind?.let {
                           (it.windSpeed * 1.609).format(3)
                       }
                   }
                   "mph" -> {
                       weatherResponse.wind?.let {
                           (it.windSpeed * 0.621).format(3)
                       }
                   }
                   else -> {
                       weatherResponse.wind?.let {
                           (it.windSpeed * 1.609).format(3)
                       }
                   }
               }
           }

           "ar" -> {
               when (speedUnit) {
                   "كم/س" -> {
                       weatherResponse.wind?.let {
                           (it.windSpeed * 1.609).format(3)
                       }
                   }

                   "ميل/س" -> {
                       weatherResponse.wind?.let {
                           (it.windSpeed * 0.621).format(3)
                       }
                   }

                   else -> {
                       weatherResponse.wind?.let {
                           (it.windSpeed * 1.609).format(3)
                       }
                   }
               }
           }
           else -> {
               weatherResponse.wind?.let {
                   (it.windSpeed * 1.609).format(3)
               }
           }
       }
    }

    fun getAppLatitude(lat: Double, context: Context): Double {
        var latitude = lat
        val locationType = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .getString("Location", "GPS")
        when (locationType) {
            "Map" -> {
                val sharedPreferences =
                    context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
                latitude = sharedPreferences.getFloat("Latitude", 0f).toDouble()
            }

            "GPS" -> latitude = lat
        }
        return latitude
    }

    fun getAppLongitude(lon: Double, context: Context): Double {
        var longitude = lon
        val locationType = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
            .getString("Location", "GPS")
        when (locationType) {
            "Map" -> {
                val sharedPreferences =
                    context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
                longitude = sharedPreferences.getFloat("Longitude", 0f).toDouble()
            }

            "GPS" -> longitude = lon
        }
        return longitude
    }

    fun filterForecastList(forecastList: List<Forecast>): List<Forecast> {
        return forecastList.filter {
            it.dt_txt?.endsWith("12:00:00") ?: false
        }
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
                "Unspecified"
            }
        } else {
            "Unspecified"
        }
    }

    fun getUnit(units: String, context: Context): String {
        when (units) {
            "metric" -> {
                return context.getString(R.string.c)
            }

            "kelvin" -> {
                return context.getString(R.string.k)
            }

            "imperial" -> {
                return context.getString(R.string.f)
            }

            else -> {
                return context.getString(R.string.c)
            }
        }
    }

    fun getDayName(dt: Long): String {
        val date = Date(dt * 1000L) // Convert seconds to milliseconds
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(date)
    }

    fun getWeatherIcon(iconCode: String): Int {
        return when (iconCode) {
            "01d" -> R.drawable.dclearsky
            "01n" -> R.drawable.nclearsky
            "02d" -> R.drawable.dfewcloud
            "02n" -> R.drawable.nfewcloud
            "03d", "03n" -> R.drawable.scatterdclouds
            "04d", "04n" -> R.drawable.brokenclouds
            "09d", "09n" -> R.drawable.showerrain
            "10d" -> R.drawable.rainday
            "10n" -> R.drawable.rainnight
            "11d", "11n" -> R.drawable.thunderstorm
            "13d", "13n" -> R.drawable.snow
            "50d", "50n" -> R.drawable.mist
            else -> R.drawable.sunrise
        }

    }

}

class HomeFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)