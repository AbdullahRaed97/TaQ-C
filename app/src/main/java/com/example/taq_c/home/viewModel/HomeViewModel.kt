package com.example.taq_c.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

    fun getCurrentWeatherData(lat: Double,lon: Double,units: String){
        viewModelScope.launch {
            try{
               weatherRepository.getCurrentWeatherData(lat = lat, lon = lon, units = units)
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
    fun get5D_3HForecastData(lat: Double,lon: Double,units: String){
        viewModelScope.launch {
            try{
                weatherRepository.get5D_3HForeCastData(lat = lat , lon = lon , units = units)
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
}

class HomeFactory(private val repository: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}