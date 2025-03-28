package com.example.taq_c.home.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.taq_c.R
import com.example.taq_c.data.model.Forecast
import com.example.taq_c.data.model.ForecastResponse
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.home.viewModel.HomeFactory
import com.example.taq_c.home.viewModel.HomeViewModel

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val hasNews: Boolean,
    val navigationAction: () -> Unit
)

@Composable
fun HomeScreen(lat: Double, lon: Double) {

    val context = LocalContext.current
    val weatherRepository = WeatherRepository.getInstance(context)
    val homeViewModel = viewModel<HomeViewModel>(
        factory = HomeFactory(weatherRepository)
    )
    val currentLocation = homeViewModel.currentLocation
    val appTempUnit = homeViewModel.getAppUnit(context)
    val appLanguage = homeViewModel.getAppLanguage(context)
    val appLatitude = homeViewModel.getAppLatitude(lat, context)
    val appLongitude = homeViewModel.getAppLongitude(lon, context)
    LaunchedEffect(currentLocation.value) {
        homeViewModel.getCurrentWeatherData(
            lat = appLatitude,
            lon = appLongitude,
            units = appTempUnit,
            lang = appLanguage
        )
        homeViewModel.get5D_3HForecastData(
            lat = appLatitude,
            lon = appLongitude,
            units = appTempUnit,
            lang = appLanguage
        )
        homeViewModel.setLatitude(context, currentLocation.value.latitude)
        homeViewModel.setLongitude(context, currentLocation.value.longitude)
    }
    val weatherResponse = homeViewModel.weatherResponse.collectAsStateWithLifecycle().value
    val forecastResponse = homeViewModel.forecastResponse.collectAsStateWithLifecycle().value
    val message = homeViewModel.message
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .background(Color(0xFF182354))
            .verticalScroll(scrollState)
            .padding(),

        ) {
        when (weatherResponse) {
            is Response.Success<WeatherResponse> -> {
                WeatherResponseData(weatherResponse.data, units = appTempUnit, homeViewModel)
            }

            is Response.Failure -> {
                LaunchedEffect(weatherResponse) {
                    snackBarHostState.showSnackbar(
                        message = weatherResponse.exception.message.toString(),
                        duration = SnackbarDuration.Short
                    )
                }
            }

            is Response.Loading -> {
                CircularIndicator()
            }

            else -> Log.i("TAG", "HomeScreen: Something went wrong")
        }

        Spacer(Modifier.height(30.dp))
        Text(
            text = stringResource(R.string.hourly_details),
            color = Color.White,
            fontSize = 28.sp,
            modifier = Modifier.padding(start = 20.dp)
        )
        Spacer(Modifier.padding(start = 20.dp))
        when (forecastResponse) {
            is Response.Failure -> LaunchedEffect(forecastResponse) {
                snackBarHostState.showSnackbar(
                    message = forecastResponse.exception.message.toString(),
                    duration = SnackbarDuration.Short
                )
                Log.i("TAG", "HomeScreen: forecast failure ${forecastResponse.exception.message}")
            }

            is Response.Loading -> {
                CircularIndicator()
            }

            is Response.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(15.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val list = forecastResponse.data.weatherForecastList
                        if (list != null) {
                            itemsIndexed(list) { index, item ->
                                HomeLazyRowItem(item, appTempUnit, homeViewModel)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = stringResource(R.string._5_days_forecast),
                        color = Color.White,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (forecastResponse.data.weatherForecastList != null) {
                            val forecastList =
                                homeViewModel.filterForecastList(forecastResponse.data.weatherForecastList)
                            forecastList.forEachIndexed { index, item ->
                                HomeLazyColumnItem(
                                    forecastResponse.data,
                                    appTempUnit,
                                    index,
                                    homeViewModel
                                )
                            }
                        }
                    }
                }
            }

            else -> Log.i("TAG", "HomeScreen: Something went wrong")
        }
    }
}

@Composable
private fun WeatherResponseData(
    weatherResponse: WeatherResponse,
    units: String,
    homeViewModel: HomeViewModel
) {
    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 5.dp, top = 20.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Text(
                text = weatherResponse.weather?.get(0)?.fullWeatherDesc.toString(),
                color = Color.White,
                fontSize = 18.sp
            )
            Text(
                text = weatherResponse.weatherDetails?.feels_like.toString(),
                color = Color.White,
                fontSize = 18.sp
            )
        }
        Spacer(Modifier.width(150.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text(
                text = stringResource(R.string.today),
                color = Color.White,
                fontSize = 18.sp,
            )
            Text(
                text = homeViewModel.convertTimeStampToDate(weatherResponse.dt),
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
    Spacer(Modifier.height(30.dp))
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row {
                //Temp
                Text(
                    text = weatherResponse.weatherDetails?.temp.toString(),
                    color = Color.White,
                    fontSize = 48.sp
                )
                //Unit
                Text(
                    text = homeViewModel.getUnit(units),
                    color = Color.White,
                    fontSize = 32.sp
                )
            }
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Country
                Text(
                    text = homeViewModel.getCountryName(weatherResponse.sys?.country),
                    color = Color.White,
                    fontSize = 18.sp
                )
                Text(
                    text = ",",
                    color = Color.White,
                    fontSize = 18.sp
                )
                //City
                Text(
                    text = weatherResponse.cityName.toString(),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 5.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //SunsetIcon
            Row {
                Icon(
                    painter = painterResource(R.drawable.sunset),
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                //Sunset
                Text(
                    text = stringResource(R.string.sunset),
                    color = Color.White,
                    fontSize = 18.sp
                )
                //Sunset Time
                Text(
                    text = homeViewModel.convertTimeStampToDate(weatherResponse.sys?.sunset ?: 0),
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
            //SunriseIcon
            Row {
                Icon(
                    painter = painterResource(R.drawable.sunrise),
                    tint = Color.White,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                //Sunrise
                Text(
                    text = stringResource(R.string.sunrise),
                    color = Color.White,
                    fontSize = 18.sp
                )
                //Sunrise Time
                Text(
                    text = homeViewModel.convertTimeStampToTime(weatherResponse.sys?.sunrise ?: 0),
                    color = Color.White,
                    fontSize = 18.sp,
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    WeatherCard(weatherResponse, homeViewModel)
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HomeLazyRowItem(
    weatherResponse: Forecast,
    units: String,
    homeViewModel: HomeViewModel
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(180.dp)
            .border(
                color = Color.Black,
                shape = RoundedCornerShape(8.dp),
                width = 2.dp
            ), elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(Color.Gray)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = homeViewModel.convertTimeStampToTime(weatherResponse.dt),
                color = Color.Black,
                fontSize = 18.sp
            )
            Spacer(Modifier.height(20.dp))
            GlideImage(
                model = "https://openweathermap.org/img/wn/" + "${weatherResponse.weather?.get(0)?.weatherIcon}@3x.png",
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weatherResponse.weatherDetails?.temp.toString(),
                    color = Color.Black,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = homeViewModel.getUnit(units),
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun CircularIndicator() {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HomeLazyColumnItem(
    forecastResponse: ForecastResponse,
    units: String,
    index: Int,
    homeViewModel: HomeViewModel
) {
    Card(
        modifier = Modifier
            .height(160.dp)
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(Color.Gray),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly
            ) {
                //Day name
                Log.i("TAG", "HomeLazyColumnItem: $index")
                Text(
                    text = homeViewModel.getDayName(
                        (forecastResponse.weatherForecastList?.get(index)?.dt ?: 0) + 86400
                    ),
                    fontSize = 25.sp,
                    color = Color.White
                )
                Text(
                    text = homeViewModel.convertTimeStampToDate(
                        forecastResponse.weatherForecastList?.get(
                            index
                        )?.dt ?: 0
                    ),
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
            GlideImage(
                model = "https://openweathermap.org/img/wn/" + "${
                    forecastResponse.weatherForecastList?.get(
                        index
                    )?.weather?.get(0)?.weatherIcon
                }@3x.png",
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //Temp
                Text(
                    text = forecastResponse.weatherForecastList?.get(index)?.weatherDetails?.temp.toString(),
                    fontSize = 18.sp,
                    color = Color.White
                )
                //Unit
                Text(
                    text = homeViewModel.getUnit(units),
                    color = Color.White,
                    fontSize = 10.sp
                )
                Text(
                    text = "/",
                    fontSize = 18.sp
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.feels_like),
                    fontSize = 18.sp,
                    color = Color.White
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //Feels_like
                    Text(
                        text = forecastResponse.weatherForecastList?.get(index)?.weatherDetails?.feels_like.toString(),
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    //Unit
                    Text(
                        text = homeViewModel.getUnit(units),
                        color = Color.White,
                        fontSize = 8.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherCard(weatherResponse: WeatherResponse, homeViewModel: HomeViewModel) {
    val context = LocalContext.current
    val speedUnit = homeViewModel.getAppWindSpeedUnit(context)
    val windSpeed = homeViewModel.calculateWindSpeed(speedUnit, weatherResponse)
    Card(
        modifier = Modifier
            .height(160.dp)
            .padding(start = 15.dp, end = 15.dp), elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(Color.Gray)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 10.dp, start = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.pressure),
                    fontSize = 24.sp
                )
                Row(
                    modifier = Modifier.padding(start = 5.dp)
                ) {
                    Text(
                        text = weatherResponse.weatherDetails?.pressure.toString(),
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.hpa),
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = stringResource(R.string.humidity),
                    fontSize = 24.sp
                )
                Text(
                    text = weatherResponse.weatherDetails?.humidity.toString() + "%",
                    fontSize = 20.sp
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(top = 10.dp, start = 80.dp, end = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.wind_speed),
                    fontSize = 24.sp
                )
                Row(
                    modifier = Modifier.padding(start = 5.dp)
                ) {
                    Text(
                        text = windSpeed,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = speedUnit,
                        fontSize = 16.sp
                    )
                }
                Text(
                    text = stringResource(R.string.clouds),
                    fontSize = 24.sp
                )
                Text(
                    text = weatherResponse.clouds?.cloudPercentage.toString() + "%",
                    fontSize = 20.sp
                )
            }
        }
    }
}

