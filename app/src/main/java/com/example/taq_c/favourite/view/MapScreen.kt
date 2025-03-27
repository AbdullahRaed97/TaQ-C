package com.example.taq_c.favourite.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taq_c.R
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.favourite.viewModel.FavouriteFactory
import com.example.taq_c.favourite.viewModel.FavouriteViewModel
import com.example.taq_c.home.view.CircularIndicator
import com.example.taq_c.home.view.getCountryName
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.io.IOException

@Composable
fun ShowMap() {
    var defaultLocation by remember { mutableStateOf(LatLng(30.0444,  31.2357))}// Cairo coordinates
    val cameraPositionState = rememberCameraPositionState { position = CameraPosition.fromLatLngZoom(defaultLocation, 10f) }
    var markerTitle by remember {mutableStateOf("Egypt")}
    val context = LocalContext.current
    var favViewModel = viewModel<FavouriteViewModel>(factory = FavouriteFactory(WeatherRepository.getInstance(context)))

    LaunchedEffect(defaultLocation) {
        cameraPositionState.animate(update = CameraUpdateFactory.newLatLngZoom(defaultLocation,10f))
        markerTitle = getCountryName(context, defaultLocation.latitude, defaultLocation.longitude) ?: ""
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = true,
            ),
            uiSettings = MapUiSettings(
                zoomControlsEnabled = true,
                compassEnabled = true,
            ),
            onMapClick = { coordinates ->
                defaultLocation = coordinates

            }
        ) {
            Marker(
                state = MarkerState(position = defaultLocation),
                title = markerTitle,
            )
        }
    }
    ShowCardDetails(defaultLocation.latitude, defaultLocation.longitude, favViewModel)
}

@Composable
fun MapScreen(navController: NavController) {
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        hasLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasLocationPermission) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }
    if (hasLocationPermission) {
        ShowMap()
    } else {
        Text("Location permission required")
    }
}

@Composable
fun ShowCardDetails(lat : Double, lon : Double ,favViewModel: FavouriteViewModel){
    val forecastResponse  =  favViewModel.forecastResponse.collectAsStateWithLifecycle().value
    favViewModel.get5D_3HForeCastData(lat = lat, lon = lon, units = "metric")
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom) {
        when(forecastResponse){
            is Response.Loading -> {
                CircularIndicator()
            }
            is Response.Failure -> {
                Text(text = "${forecastResponse.exception.message}",
                    fontSize = 50.sp,
                    color = Color.Black
                )
            }
            is Response.Success ->{
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 15.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(Color.Gray),
                    shape = RoundedCornerShape(15)
                ) {
                    //Lat
                        Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp , horizontal = 15.dp)
                    ){
                        Text(
                            text = stringResource(R.string.latitude),
                            fontSize = 25.sp,
                            color = Color.White,
                            modifier = Modifier
                                .padding(vertical = 10.dp)
                                .padding(end = 5.dp)
                        )
                        Text(
                            text = forecastResponse.data.city?.coord?.lat.toString(),
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp , horizontal = 15.dp)
                    ){
                        Text(
                            text = stringResource(R.string.longitude),
                            fontSize = 25.sp,
                            color = Color.White,
                            modifier = Modifier.padding(end=10.dp)
                        )
                        Text(
                            text = forecastResponse.data.city?.coord?.lon.toString(),
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp , horizontal = 15.dp)
                    ){
                        Text(
                            text = stringResource(R.string.country_name),
                            fontSize = 25.sp,
                            color = Color.White,
                            modifier = Modifier.padding(end=10.dp)
                        )
                        Text(
                            text =getCountryName(forecastResponse.data.city?.country),
                            fontSize = 25.sp,
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = {
                          forecastResponse.data.city?.let {
                              favViewModel.insertFavCity(it)
                              Log.i("TAG", "ShowDialog: $it")
                          }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(Color.Black)
                    ) {
                        Text(
                            text = "Add To Favourite",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

fun getCountryName(context: Context, latitude: Double, longitude: Double): String? {
    val geocoder = Geocoder(context)
    return try {
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses?.isNotEmpty() == true) {
            addresses[0].countryName
        } else {
            null
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}


