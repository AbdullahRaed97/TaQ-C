package com.example.taq_c.main

import android.Manifest
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.example.taq_c.main.view.ApplicationScreens
import com.example.taq_c.utilities.LocationHelper
import com.example.taq_c.utilities.NetworkManagement

class MainActivity : ComponentActivity() {

    lateinit var networkCallback : ConnectivityManager.NetworkCallback
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         var isNetworkAvailable : MutableState<Boolean?> = mutableStateOf(null)
        val networkManagement = NetworkManagement(this)
        if(networkManagement.isNetworkAvailable()){
            isNetworkAvailable.value = true
        }else{
            isNetworkAvailable.value = false
        }
        networkCallback = object : ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                isNetworkAvailable.value = true
            }

            override fun onLost(network: Network) {
                isNetworkAvailable.value = false
            }
        }
        setContent {
            ApplicationScreens(isNetworkAvailable.value)
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
        val networkManagement = NetworkManagement(this)
        networkManagement.registerNetworkCallback(networkCallback)
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

    override fun onStop() {
        super.onStop()
        val networkManagement = NetworkManagement(this)
        networkManagement.unregisterNetworkCallback(networkCallback)
    }
}

