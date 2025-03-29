package com.example.taq_c

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

object LocationHelper {
    //class that will return the location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    //Because location will always changed
    var locationState: MutableState<Location> = mutableStateOf(Location(LocationManager.GPS_PROVIDER))
    //REQUEST_CODE
    val REQUEST_CODE = 0

     fun checkPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context,
            android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
    }
    fun locationEnabled(context: Context):Boolean{
        val locationManager: LocationManager
                = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
    @SuppressLint("MissingPermission")
    fun getFreshLocation(context: Context){
        //entry point
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(context)
        //get the new location
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMinUpdateDistanceMeters(1000f)
            }.build(),
            object: LocationCallback(){
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    locationState.value=p0.lastLocation?: Location(LocationManager.GPS_PROVIDER)
                    Log.i("TAG", "onLocationResult: ${p0.lastLocation.toString()}")
                }
            },
            Looper.myLooper()
        )
    }
    fun enableLocationService(context: Context){
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        context.startActivity(intent)
    }

    fun getLatitude(context: Context): Double{
        val sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("Latitude",0).toDouble()
    }

    fun getLongitude(context: Context): Double{
        val sharedPreferences = context.getSharedPreferences("Coordinates", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("Longitude",0).toDouble()
    }
}