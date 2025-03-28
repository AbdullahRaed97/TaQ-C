package com.example.taq_c.favourite.viewModel

import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class FavouriteViewModel(private val weatherRepository: WeatherRepository): ViewModel() {
    private val favCitiesResponse_: MutableStateFlow<Response<List<City>?>> =
        MutableStateFlow(Response.Loading)
    val favCitiesResponse = favCitiesResponse_.asStateFlow()
    private val forecastResponse_ : MutableStateFlow<Response<ForecastResponse>> =
        MutableStateFlow(Response.Loading)
    val forecastResponse =
        forecastResponse_.asStateFlow()
    private val message_: MutableSharedFlow<String?> = MutableSharedFlow()
    val message = message_.asSharedFlow()

    fun getAllFavCities() {
        viewModelScope.launch {
            try {
                weatherRepository.getAllFavCities()
                    .catch {
                        message_.emit(it.message)
                        favCitiesResponse_.emit(Response.Failure(it))
                    }.collect {
                        favCitiesResponse_.emit(Response.Success(it))
                    }
            } catch (e: Exception) {
                message_.emit(e.message)
                favCitiesResponse_.emit(Response.Failure(e))
            }
        }
    }
        fun insertFavCity(city: City) {
            viewModelScope.launch {
                try {
                    val result = weatherRepository.insertFavCity(city)
                    if (result > 0) {
                        message_.emit("Insertion Success")
                    } else {
                        message_.emit("Error : Failed to insert city")
                    }
                } catch (e: Exception) {
                    message_.emit(e.message)
                }
            }
        }

        fun deleteFavCity(city: City) {
            viewModelScope.launch {
                try {
                    val result = weatherRepository.deleteFavCity(city)
                    if (result > 0) {
                        message_.emit("Deletion Success")

                    } else {
                        message_.emit("Unable to delete City")
                    }
                } catch (e: Exception) {
                    message_.emit(e.message)
                }
            }
        }

        fun get5D_3HForeCastData(lat: Double, lon: Double, units: String,lang: String) {
            viewModelScope.launch {
                try {
                    weatherRepository.get5D_3HForeCastData(lat = lat, lon = lon, units = units, lang = lang)
                        .catch {
                            forecastResponse_.emit(Response.Failure(it))
                            message_.emit(it.message.toString())
                        }.collect {
                            forecastResponse_.emit(Response.Success<ForecastResponse>(it as ForecastResponse))
                        }
                } catch (e: Exception) {
                    forecastResponse_.emit(Response.Failure(e))
                    message_.emit(e.message.toString())
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

    fun getCountryName(context: Context, latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context)
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                addresses[0].countryName
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun getCountryName(countryCode: String?): String {
        return Locale("", countryCode).displayCountry ?: "UnSpecified"
    }

}

class FavouriteFactory(private val repository: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(repository) as T
    }
}
