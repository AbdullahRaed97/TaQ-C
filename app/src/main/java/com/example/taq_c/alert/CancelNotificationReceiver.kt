package com.example.taq_c.alert

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.taq_c.alert.viewModel.AlertViewModel
import com.example.taq_c.data.db.WeatherDatabase
import com.example.taq_c.data.local.WeatherLocalDataSource
import com.example.taq_c.data.remote.RetrofitHelper
import com.example.taq_c.data.remote.WeatherRemoteDataSource
import com.example.taq_c.data.repository.WeatherRepository

class CancelNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val notificationID = intent?.getIntExtra("notificationID", 0)
        val timeStamp = intent?.getLongExtra("timeStamp",0)?:0
        Log.i("TAG", "onReceive: $timeStamp")
        val weatherRepository = WeatherRepository.getInstance(
            WeatherLocalDataSource
                .getInstance(
                    WeatherDatabase
                        .getInstance(context).getWeatherDao(), WeatherDatabase
                        .getInstance(context).getAlertDao(),
                    WeatherDatabase.getInstance(context).getResponsesDao()
                ),
            WeatherRemoteDataSource
                .getInstance(RetrofitHelper.weatherService)
        )
        val alertViewModel = AlertViewModel(weatherRepository)
        alertViewModel.deleteAlertByTime(timeStamp,context)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationID ?: 0)

    }
}