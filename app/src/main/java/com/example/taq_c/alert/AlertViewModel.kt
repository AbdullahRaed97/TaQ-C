package com.example.taq_c.alert

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Locale
import java.util.concurrent.TimeUnit

class AlertViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val alertResponse_: MutableStateFlow<Response<List<Alert>?>> =
        MutableStateFlow(Response.Loading)
    val alertResponse = alertResponse_.asStateFlow()
    private val forecastResponse_ : MutableStateFlow<Response<ForecastResponse>> =
        MutableStateFlow(Response.Loading)
    val forecastResponse = forecastResponse_.asStateFlow()

    fun getAllAlert() {
        viewModelScope.launch {
            try {
                weatherRepository.getAllAlerts()
                    .catch {
                        alertResponse_.emit(Response.Failure(it))
                    }.collect {
                        alertResponse_.emit(Response.Success(it))
                    }
            } catch (e: Exception) {
                alertResponse_.emit(Response.Failure(e))
            }
        }
    }

    fun getForeCastResponse(lat: Double,lon: Double){
        viewModelScope.launch {
            try{
                weatherRepository.get5D_3HForeCastData(lat,lon,"metric","en")
                    .catch {
                    forecastResponse_.emit(Response.Failure(it))
                }.collect {
                    forecastResponse_.emit(Response.Success(it as ForecastResponse))
                    }
            }catch (e: Exception){
                forecastResponse_.emit(Response.Failure(e))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun requestAlert(context: Context, alert: Alert, hour:Int, minute:Int, timeStamp :Long ) {

        val data = Data.Builder()
            .putDouble("lat", alert.city.coord?.lat?:0.0)
            .putDouble("lon", alert.city.coord?.lon?:0.0)
            .build()

        val instant = Instant.ofEpochMilli(timeStamp)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val day = localDateTime.dayOfMonth
        val month = localDateTime.monthValue
        val year = localDateTime.year

        val duration = calculateTheDelay( hour = hour, minute = minute, day = day , month = month , year = year )
        Log.i("TAG", "requestAlert: $duration")
        val alertRequest = OneTimeWorkRequestBuilder<AlertWorker>()
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .addTag(alert.requestCode.toString())
            .setInputData(data)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueue(alertRequest)
        val result = WorkManager.getInstance(context).getWorkInfoByIdLiveData(alertRequest.id).observe(context as LifecycleOwner){
            when(it.state){
                WorkInfo.State.ENQUEUED -> Log.i("TAG", "requestAlert: enqueued")
                WorkInfo.State.RUNNING -> Log.i("TAG", "requestAlert: Running")
                WorkInfo.State.SUCCEEDED -> Log.i("TAG", "requestAlert: Succeed")
                WorkInfo.State.FAILED -> {Log.i("TAG", "requestAlert: Failed")
                val error = it.outputData.getString("Error")
                    Log.i("TAG", "requestAlert: $error")
                }
                WorkInfo.State.BLOCKED -> Log.i("TAG", "requestAlert: Blocked")
                WorkInfo.State.CANCELLED ->Log.i("TAG", "requestAlert: cancelled")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateTheDelay(hour:Int, minute:Int, day:Int, month:Int, year :Int):Long{
        val chosenTime = LocalDateTime.of(year,month,day,hour,minute)
        val currentDate = LocalDateTime.now()
        Log.i("TAG", "calculateTheDelay: $chosenTime now : $currentDate")
        val duration = java.time.Duration.between(currentDate,chosenTime)
        return duration.toMillis().coerceAtLeast(0)
    }

    fun deleteAlert(context: Context,alert:Alert){
        viewModelScope.launch {
            try {
                weatherRepository.deleteAlert(alert)
                WorkManager.getInstance(context).cancelAllWorkByTag(alert.requestCode.toString())
            }catch (e : Exception){
                alertResponse_.emit(Response.Failure(e))
            }
        }
    }

    fun insertAlert(alert:Alert){
        viewModelScope.launch {
            try {
                weatherRepository.insertAlert(alert)
            }catch (e : Exception){
                alertResponse_.emit(Response.Failure(e))
            }
        }
    }

    fun getCountryName(countryCode: String?): String {
        return Locale("", countryCode).displayCountry ?: "UnSpecified"
    }

}

class AlertFactory(val weatherRepository: WeatherRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(weatherRepository) as T
    }
}

class AlertWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters) {
    val weatherRepository = WeatherRepository.getInstance(context)
    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {
        try {
            val lat = inputData.getDouble("lat", 0.0)
            val lon = inputData.getDouble("lon", 0.0)
            Log.i("TAG", "doWork: $lat , $lon")
            val weatherResponse =
                try {
                    weatherRepository.getCurrentWeatherData(lat, lon, "metric", "en").first()
                } catch (e: Exception) {
                    Log.i("TAG", "doWork: api call fail : ${e.message}")
                    return Result.Failure(workDataOf("Error" to e.message))
                }
            Log.i("TAG", "doWork: ${weatherResponse?.weather?.get(0)?.fullWeatherDesc}")
            sendNotification()
            return Result.success()
        }catch (e: Exception){
            Log.e("TAG", "doWork: ",e)
            return Result.Failure()
        }

    }
    private fun sendNotification(){

    }
}