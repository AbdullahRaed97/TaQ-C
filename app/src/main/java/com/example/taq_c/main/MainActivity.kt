package com.example.taq_c.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import com.example.taq_c.main.view.ApplicationScreens
import com.example.taq_c.utilities.LocationHelper
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        //get app language
        val sharedPreferences = newBase?.getSharedPreferences("Settings", MODE_PRIVATE)
        val appLanguage = sharedPreferences?.getString("Language", "en") ?: "en"
        //create new Locale and make it default
        val locale = Locale(appLanguage)
        Locale.setDefault(locale)
        //get the configuration of the app
        val resources = newBase?.resources
        val config = resources?.configuration
        //adjust the configuration
        config?.setLocale(locale)
        config?.setLayoutDirection(locale)
        //send the new context with the new configuration
        if (config != null) {
            super.attachBaseContext(newBase.createConfigurationContext(config))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationScreens()
        }
    }

    override fun onStart() {
        super.onStart()
        //check permission
        if (LocationHelper.checkPermission(this)) {
            //check if the location is enabled
            if (LocationHelper.locationEnabled(this)) {
                LocationHelper.getFreshLocation(this)
            } else {
                //enable the Location service
                LocationHelper.enableLocationService(this)
            }
        } else {
            //No permission supported so request permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LocationHelper.REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == LocationHelper.REQUEST_CODE) {
            if (grantResults.get(0) == PackageManager.PERMISSION_GRANTED || grantResults.get(1) == PackageManager.PERMISSION_GRANTED) {
                if (LocationHelper.locationEnabled(this)) {
                    LocationHelper.getFreshLocation(this)
                } else {
                    LocationHelper.enableLocationService(this)
                }
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LocationHelper.REQUEST_CODE
                )
            }
        }
    }
}

