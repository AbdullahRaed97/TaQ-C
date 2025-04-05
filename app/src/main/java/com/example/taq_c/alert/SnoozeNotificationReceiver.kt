package com.example.taq_c.alert

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.taq_c.alert.viewModel.AlertWorker
import java.util.concurrent.TimeUnit

class SnoozeNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {

        Log.i("TAG", "onReceive: i am here now trying to snooze")
        val lat = intent?.getDoubleExtra("lat", 0.0)
        val lon = intent?.getDoubleExtra("lon", 0.0)

        val data = Data.Builder()
            .putDouble("lat", lat ?: 0.0)
            .putDouble("lon", lon ?: 0.0)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val alertRequest = OneTimeWorkRequestBuilder<AlertWorker>()
            .setInputData(data)
            .setInitialDelay(1, TimeUnit.MINUTES)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR, 5, TimeUnit.SECONDS
            )
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context).enqueue(alertRequest)

    }
}