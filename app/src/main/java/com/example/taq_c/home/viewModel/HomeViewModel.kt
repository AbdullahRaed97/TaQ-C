package com.example.taq_c.home.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val weatherResponse_ :MutableLiveData<WeatherResponse> = MutableLiveData()
    val weatherResponse: LiveData<WeatherResponse> = weatherResponse_
    private val message_ : MutableLiveData<String> = MutableLiveData()
    val message : LiveData<String> = message_

    fun getCurrentWeatherData(lat: String,lon: String,units: String){
        viewModelScope.launch {
            try{
               val result= weatherRepository.getCurrentWeatherData(lat,lon,units)
                if(result!=null){
                    weatherResponse_.postValue(result)
                    Log.d("TAG", "getCurrentWeatherData: ")
                }else{
                    message_.postValue("Something went wrong")
                }
            }catch (e:Exception){
                message_.postValue("Error : ${e.message}")
            }
        }
    }
    fun get5D_3HForecastData(lat: String,lon: String,units: String){
        viewModelScope.launch {
            try{
                val result= weatherRepository.getCurrentWeatherData(lat,lon,units)
                if(result!=null){
                    weatherResponse_.postValue(result)
                }else{
                    message_.postValue("Something went wrong")
                }
            }catch (e:Exception){
                message_.postValue("Error : ${e.message}")
            }
        }
    }
}

class HomeFactory(private val repository: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repository) as T
    }
}