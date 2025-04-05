package com.example.taq_c

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import com.example.taq_c.utilities.WeatherNotificationService
import com.google.android.libraries.places.api.Places

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyC9hCKfEcNFR2YQDeZ3Xdn3Dk0A7z2xd_c")
        }
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            val soundUri =
                "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${applicationContext.packageName}/${R.raw.alert}".toUri()
            val channel = NotificationChannel(
                WeatherNotificationService.WEATHER_CHANNEL_ID,
                "Weather",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "This Notification is for Weather Cautions"
            channel.setSound(soundUri
                ,AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}