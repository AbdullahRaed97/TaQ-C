package com.example.taq_c.favourite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavouriteViewModel(private val weatherRepository: WeatherRepository): ViewModel() {
    private val favCitiesResponse_: MutableStateFlow<Response<List<City>?>> =
        MutableStateFlow(Response.Loading)
    val favCitiesResponse = favCitiesResponse_.asStateFlow()
    private val weatherResponse_: MutableStateFlow<Response<WeatherResponse>> =
        MutableStateFlow(Response.Loading)
    val weatherResponse = weatherResponse_.asStateFlow()
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

        fun getCurrentWeatherData(lat: Double, lon: Double, units: String) {
            viewModelScope.launch {
                try {
                    weatherRepository.getCurrentWeatherData(lat = lat, lon = lon, units = units)
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
    }
}

class FavouriteFactory(private val repository: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(repository) as T
    }
}
