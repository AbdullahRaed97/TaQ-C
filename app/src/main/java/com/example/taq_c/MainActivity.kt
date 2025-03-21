package com.example.taq_c

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.home.view.HomeScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
        lifecycleScope.launch {
            val repository : WeatherRepository = WeatherRepository.getInstance(this@MainActivity)
            try {
                val result = repository.getCurrentWeatherData("41.3874","2.1686","metric")
                //Log.i("TAG", "onCreate: ${repository.get5D_3HForeCastData(lat="31.42723",lon="35", units = "")}")
                Log.d("TAG", "onCreate: ${result}")
            }catch (e: Exception){
                Log.i("TAG", "onCreate: error is ${e.localizedMessage} ")
            }
        }
    }
//    override fun onStart() {
//        super.onStart()
//        //check permission
//        if(Location.checkPermission(this)){
//            //check if the location is enabled
//            if(Location.locationEnabled(this)){
//                Location.getFreshLocation(this)
//            }else{
//                //enable the Location service
//                Location.enableLocationService(this)
//            }
//        }else{
//            //No permission supported so request permission
//            ActivityCompat.requestPermissions(this ,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION
//                    ,android.Manifest.permission.ACCESS_COARSE_LOCATION),
//                Location.REQUEST_CODE
//            )
//        }
//    }
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray,
//        deviceId: Int
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
//        if(requestCode==Location.REQUEST_CODE){
//            if(grantResults.get(0)== PackageManager.PERMISSION_GRANTED || grantResults.get(1) == PackageManager.PERMISSION_GRANTED){
//                if(Location.locationEnabled(this)){
//                    Location.getFreshLocation(this)
//                }else{
//                    Location.enableLocationService(this)
//                }
//            }else{
//                ActivityCompat.requestPermissions(this ,
//                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION
//                        ,android.Manifest.permission.ACCESS_COARSE_LOCATION),
//                    Location.REQUEST_CODE
//                )
//            }
//
//        }
//    }package

}

