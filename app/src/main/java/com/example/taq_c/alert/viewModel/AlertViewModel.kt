package com.example.taq_c.alert.viewModel

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.BackoffPolicy
import androidx.work.Constraints.Builder
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.taq_c.alert.CancelNotificationReceiver
import com.example.taq_c.R
import com.example.taq_c.utilities.WeatherNotificationService
import com.example.taq_c.utilities.WeatherNotificationService.Companion.WEATHER_CHANNEL_ID
import com.example.taq_c.alert.SnoozeNotificationReceiver
import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.main.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.UUID
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
    fun requestAlert(context: Context, city: City, hour:Int, minute:Int, timeStamp :Long ) {

        val data = Data.Builder()
            .putDouble("lat", city.coord?.lat?:0.0)
            .putDouble("lon", city.coord?.lon?:0.0)
            .build()
       val constraints = Builder()
           .setRequiredNetworkType(NetworkType.CONNECTED)
           .build()

        val instant = Instant.ofEpochMilli(timeStamp)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val day = localDateTime.dayOfMonth
        val month = localDateTime.monthValue
        val year = localDateTime.year

        val duration = calculateTheDelay( hour = hour, minute = minute, day = day , month = month , year = year )

        val alertRequest = OneTimeWorkRequestBuilder<AlertWorker>()
            .setInitialDelay(duration, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,5, TimeUnit.SECONDS
            )
            .setConstraints(constraints)
            .build()

        val requestCode = alertRequest.id.toString()
        insertAlert(Alert(requestCode,city,timeStamp))
        WorkManager.getInstance(context).enqueue(alertRequest)
        val result = WorkManager.getInstance(context).getWorkInfoByIdLiveData(alertRequest.id).observe(context as LifecycleOwner){
            when(it.state){
                WorkInfo.State.ENQUEUED -> Log.i("TAG", "requestAlert: enqueued")
                WorkInfo.State.RUNNING -> Log.i("TAG", "requestAlert: Running")
                WorkInfo.State.SUCCEEDED -> Log.i("TAG", "requestAlert: Succeed")
                WorkInfo.State.FAILED -> {
                    Log.i("TAG", "requestAlert: Failed")
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
        val duration = Duration.between(currentDate,chosenTime)
        return duration.toMillis().coerceAtLeast(0)
    }

    fun deleteAlert(context: Context,alert:Alert){
        viewModelScope.launch {
            try {
                weatherRepository.deleteAlert(alert)
                val workID = UUID.fromString(alert.requestCode)
                WorkManager.getInstance(context).cancelWorkById(workID)
            }catch (e : Exception){
                alertResponse_.emit(Response.Failure(e))
            }
        }
    }

   private fun insertAlert(alert:Alert){
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

    fun convertTimeStampToDate(timeStamp: Long): String {
        val date = Date(timeStamp)
        val sdf = SimpleDateFormat("MMMM d,yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun checkNotificationOpened(context: Context): Boolean{
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
       return notificationManager.areNotificationsEnabled()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun allowNotification(context: Context){
        val myIntent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, WEATHER_CHANNEL_ID)
        }
        context.startActivity(myIntent)
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
    val myContext = context
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
                    return Result.retry()
                }
            Log.i("TAG", "doWork: ${weatherResponse?.weather?.get(0)?.fullWeatherDesc}")
            showNotification(myContext,weatherResponse?.weather?.get(0)?.fullWeatherDesc?:"",lat,lon)
            return Result.success()
        }catch (e: Exception){
            Log.e("TAG", "doWork: ",e)
            return Result.Failure()
        }

    }
    private fun showNotification(context: Context,content: String,lat: Double,lon: Double){
        Log.i("TAG", "showNotification: iam here now trying to build the notification")
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val soundUri =
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${applicationContext.packageName}/${R.raw.alert}".toUri()
        //Pending Intent which will be executed when clicking on the notification
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            Intent(context, MainActivity::class.java),
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) PendingIntent.FLAG_IMMUTABLE else 0
        )
        //Building Snooze Intent
        val snoozeIntent = PendingIntent.getBroadcast(
            context ,
            2,
            Intent(context, SnoozeNotificationReceiver::class.java).apply{
                putExtra("lat",lat)
                putExtra("lon",lon)
            },
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) PendingIntent.FLAG_IMMUTABLE else 0
        )
        //Building Cancel Intent
        val cancelIntent = PendingIntent.getBroadcast(
            context,
            3,
            Intent(context, CancelNotificationReceiver::class.java).apply {
                putExtra("notificationID", 1)
            },
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notification = NotificationCompat.Builder(context,WEATHER_CHANNEL_ID)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Weather Caution !")
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
            ).setContentIntent(pendingIntent)
            .addAction(
                R.drawable.notification,
                "Snooze",
                snoozeIntent
            )
            .addAction(
                R.drawable.notification,
                "Cancel",
                cancelIntent
            )
            .setSound(soundUri)
            .build()
        notificationManager.notify(1,notification)
    }
}