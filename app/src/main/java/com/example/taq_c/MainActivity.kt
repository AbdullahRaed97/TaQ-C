package com.example.taq_c

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.example.taq_c.data.remote.WeatherRemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
        lifecycleScope.launch(Dispatchers.IO) {
            val api = WeatherRemoteDataSource.weatherService
            val weatherResponse1 = api.getCurrentWeather(48.85341,2.3488)
            val weatherResponse2 = api.get5D_3HForecastData(48.85341,2.3488)
            Log.i("TAG", "CurrentWeatherResponse:${weatherResponse1} ")
            Log.i("TAG", "5D_3HForecastDataResponse:${weatherResponse2} ")
        }
    }
    override fun onStart() {
        super.onStart()
        //check permission
        if(Location.checkPermission(this)){
            //check if the location is enabled
            if(Location.locationEnabled(this)){
                Location.getFreshLocation(this)
            }else{
                //enable the Location service
                Location.enableLocationService(this)
            }
        }else{
            //No permission supported so request permission
            ActivityCompat.requestPermissions(this ,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION
                    ,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                Location.REQUEST_CODE
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
        if(requestCode==Location.REQUEST_CODE){
            if(grantResults.get(0)== PackageManager.PERMISSION_GRANTED || grantResults.get(1) == PackageManager.PERMISSION_GRANTED){
                if(Location.locationEnabled(this)){
                    Location.getFreshLocation(this)
                }else{
                    Location.enableLocationService(this)
                }
            }else{
                ActivityCompat.requestPermissions(this ,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION
                        ,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    Location.REQUEST_CODE
                )
            }

        }
    }

}

