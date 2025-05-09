package com.example.taq_c.favourite.view

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taq_c.R
import com.example.taq_c.data.db.WeatherDatabase
import com.example.taq_c.data.local.WeatherLocalDataSource
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.remote.RetrofitHelper
import com.example.taq_c.data.remote.WeatherRemoteDataSource
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.favourite.viewModel.FavouriteFactory
import com.example.taq_c.favourite.viewModel.FavouriteViewModel
import com.example.taq_c.home.view.CircularIndicator
import com.example.taq_c.utilities.NavigationRoute
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(
    fromSetting: Boolean = false,
    fromAlert: Boolean,
    navController: NavController,
    snackBarHostState: SnackbarHostState
) {
    var defaultLocation by remember { mutableStateOf(LatLng(30.0444, 31.2357)) }// Cairo coordinates
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 10f)
    }
    var markerTitle by remember { mutableStateOf("Egypt") }
    val context = LocalContext.current
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
    var favViewModel = viewModel<FavouriteViewModel>(factory = FavouriteFactory(weatherRepository))
    val message = favViewModel.message.collectAsStateWithLifecycle(initialValue = null).value

    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val place = Autocomplete.getPlaceFromIntent(result.data!!)
            defaultLocation = place.latLng
        } else if (result.resultCode == AutocompleteActivity.RESULT_ERROR) {
            val status = Autocomplete.getStatusFromIntent(result.data!!)
            Log.e("Places", "Error: ${status.statusMessage}")
        }
    }
    LaunchedEffect(message) {
        if (message != null) {
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    LaunchedEffect(defaultLocation) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(
                defaultLocation,
                10f
            )
        )
        markerTitle = favViewModel.getCountryName(
            context,
            defaultLocation.latitude,
            defaultLocation.longitude
        ) ?: ""
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                    .build(context)
                autocompleteLauncher.launch(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(Color.LightGray)
        ) {
            Icon(
                painter = painterResource(R.drawable.search),
                contentDescription = null
            )
            Text("Search for a place")
        }
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
            defaultLocation.let { latLng ->
                LaunchedEffect(latLng) {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
                Marker(
                    state = MarkerState(position = defaultLocation),
                    title = markerTitle,
                )
            }
        }
    }
    if (fromSetting) {
        FromSettingConfiguration(
            favViewModel,
            navController,
            defaultLocation.latitude,
            defaultLocation.longitude
        )
    } else if (fromAlert) {
        FromAlertConfiguration(navController, defaultLocation.latitude, defaultLocation.longitude)
    } else {
        ShowCardDetails(
            defaultLocation.latitude,
            defaultLocation.longitude,
            favViewModel,
            snackBarHostState
        )
    }
}

@Composable
fun ShowCardDetails(
    lat: Double,
    lon: Double,
    favViewModel: FavouriteViewModel,
    snackBarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val appUnit = favViewModel.getAppUnit(context)
    val appLanguage = favViewModel.getAppLanguage(context)
    val forecastResponse = favViewModel.forecastResponse.collectAsStateWithLifecycle().value
    val message = favViewModel.message.collectAsStateWithLifecycle(initialValue = null).value
    favViewModel.get5D_3HForeCastData(lat = lat, lon = lon, units = appUnit, lang = appLanguage)
    LaunchedEffect(message) {
        if (message != null) {
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        when (forecastResponse) {
            is Response.Loading -> {
                CircularIndicator()
            }

            is Response.Failure -> {
                Text(
                    text = "${forecastResponse.exception.message}",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }

            is Response.Success -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 20.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(Color(0xFF424242)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.latitude),
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = forecastResponse.data.city?.coord?.lat.toString(),
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.longitude),
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = forecastResponse.data.city?.coord?.lon.toString(),
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.country_name),
                                fontSize = 20.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = favViewModel.getCountryName(forecastResponse.data.city?.country),
                                fontSize = 18.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Button(
                            onClick = {
                                forecastResponse.data.city?.let {
                                    favViewModel.insertFavCity(it)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E88E5),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.add_to_favourite),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FromSettingConfiguration(
    favViewModel: FavouriteViewModel,
    navController: NavController,
    lat: Double,
    lon: Double
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = {
                favViewModel.setAppLatitude(context, lat)
                favViewModel.setAppLongitude(context, lon)
                navController.navigate(NavigationRoute.HomeScreen(lat,lon))
            },
            colors = ButtonDefaults.buttonColors(Color.Black)

        ) {
            Text(
                text = stringResource(R.string.set_your_location),
                color = Color.White,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun FromAlertConfiguration(navController: NavController, lat: Double, lon: Double) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = {
                navController.navigate(NavigationRoute.SetAlertScreen(lat, lon))
            },
            colors = ButtonDefaults.buttonColors(Color.Black)

        ) {
            Text(
                text = stringResource(R.string.set_your_destination),
                color = Color.White,
                fontSize = 20.sp
            )
        }
    }
}

