package com.example.taq_c.favourite.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.repository.WeatherRepository
import kotlinx.coroutines.launch

class FavouriteViewModel(private val weatherRepository: WeatherRepository): ViewModel() {
    private val favCities_ : MutableLiveData<List<City>?> = MutableLiveData()
    val favCities : LiveData<List<City>?> = favCities_
    private val message_ : MutableLiveData<String?> = MutableLiveData()
    val message : LiveData<String?> = message_
    private val weatherResponse_ : MutableLiveData<WeatherResponse> = MutableLiveData()
    val weatherResponse : LiveData<WeatherResponse> = weatherResponse_

    fun getAllFavCities(){
        viewModelScope.launch {
            try {
                val result = weatherRepository.getAllFavCities()
                if(result!=null){
                    favCities_.postValue(result)
                }else{
                    message_.postValue("Something went wrong")
                }
            }catch (e: Exception){
                message_.postValue("Error : ${e.message}")
            }
        }
    }
    fun insertFavCity(city: City){
        viewModelScope.launch {
            try {
                val result = weatherRepository.insertFavCity(city)
                if (result>0){
                    message_.postValue("Insertion Success")
                }else{
                    message_.postValue("Error : Failed to insert city")
                }
            }catch (e: Exception){
                message_.postValue("Error : ${e.message}")
            }
        }
    }
    fun deleteFavCity(city: City){
        viewModelScope.launch {
            try{
                val result = weatherRepository.deleteFavCity(city)
                if (result>0){
                    message_.postValue("Deletion Success")
                    val newList = favCities_.value?.toMutableList()
                    newList?.remove(city)
                    favCities_.postValue(newList)
                }
                else{
                    message_.postValue("Unable to delete City")
                }
            }catch (e: Exception){
                message_.postValue("Error : ${e.message}")
            }
        }
    }
    fun getCurrentWeatherData(lat: String,lon: String,units: String){
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

class FavouriteFactory(private val repository: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(repository) as T
    }
}