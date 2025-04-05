package com.example.taq_c.home.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.taq_c.R
import com.example.taq_c.data.db.WeatherDatabase
import com.example.taq_c.data.local.WeatherLocalDataSource
import com.example.taq_c.data.model.Forecast
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.model.WeatherResponse
import com.example.taq_c.data.remote.RetrofitHelper
import com.example.taq_c.data.remote.WeatherRemoteDataSource
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

@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(
    lat: Double,
    lon: Double,
    isNetworkAvailable: Boolean?,
    snackBarHostState: SnackbarHostState,
    dayState: MutableState<String>
) {
    val context = LocalContext.current
    val weatherRepository = WeatherRepository.getInstance(
        WeatherLocalDataSource
            .getInstance(
                WeatherDatabase
                    .getInstance(context).getWeatherDao(), WeatherDatabase
                    .getInstance(context).getAlertDao()
            ),
        WeatherRemoteDataSource
            .getInstance(RetrofitHelper.weatherService)
    )
    val homeViewModel = viewModel<HomeViewModel>(
        factory = HomeFactory(weatherRepository)
    )
    val currentLocation = homeViewModel.currentLocation
    val appTempUnit = homeViewModel.getAppUnit(context)
    val appLanguage = homeViewModel.getAppLanguage(context)
    val appLatitude = homeViewModel.getAppLatitude(lat, context)
    val appLongitude = homeViewModel.getAppLongitude(lon, context)
    val message = homeViewModel.message.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(message) {
        if (message != null) {
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    LaunchedEffect(appLatitude, currentLocation.value) {

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
    }

    val weatherResponse = homeViewModel.weatherResponse.collectAsStateWithLifecycle().value
    val forecastResponse = homeViewModel.forecastResponse.collectAsStateWithLifecycle().value
    val snackBarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .verticalScroll(scrollState)
    ) {
        when (weatherResponse) {
            is Response.Success<WeatherResponse> -> {
                WeatherResponseData(
                    weatherResponse.data,
                    units = appTempUnit,
                    homeViewModel
                )
                dayState.value = weatherResponse.data.weather?.get(0)?.weatherIcon ?: "01d"
            }

            is Response.Failure -> {}
            is Response.Loading -> {
                CircularIndicator()
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.hourly_details),
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        when (forecastResponse) {
            is Response.Failure -> {}
            is Response.Loading -> {
                CircularIndicator()
            }

            is Response.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val list = forecastResponse.data.weatherForecastList
                        if (list != null) {
                            itemsIndexed(list) { index, item ->
                                HomeLazyRowItem(item, appTempUnit, homeViewModel)
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        text = stringResource(R.string._5_days_forecast),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (forecastResponse.data.weatherForecastList != null) {
                            val forecastList =
                                homeViewModel.filterForecastList(forecastResponse.data.weatherForecastList)
                            forecastList.forEachIndexed { index, item ->
                                HomeLazyColumnItem(
                                    item,
                                    appTempUnit,
                                    homeViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherResponseData(
    weatherResponse: WeatherResponse,
    units: String,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = weatherResponse.weather?.get(0)?.fullWeatherDesc?.toString() ?: "",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.feels_like),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = "${weatherResponse.weatherDetails?.feels_like} " + homeViewModel.getUnit(
                            units,
                            context
                        ),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 16.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.today),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = homeViewModel.convertTimeStampToDate(weatherResponse.dt),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(
                    homeViewModel.getWeatherIcon(weatherResponse.weather?.get(0)?.weatherIcon ?: "")
                ),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = weatherResponse.weatherDetails?.temp?.toString() ?: "--",
                    color = Color.White,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Light
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = homeViewModel.getUnit(units, context),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light
                )
            }
            Spacer(Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = homeViewModel.getCountryName(weatherResponse.sys?.country),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = ",",
                    color = Color.White,
                    fontSize = 20.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = weatherResponse.cityName ?: "",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.sunrise),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.sunrise),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = homeViewModel.convertTimeStampToTime(weatherResponse.sys?.sunrise ?: 0),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.sunset),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.sunset),
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = homeViewModel.convertTimeStampToTime(weatherResponse.sys?.sunset ?: 0),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
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
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(200.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xB3424242)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = homeViewModel.convertTimeStampToTime(weatherResponse.dt),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Image(
                painter = painterResource(
                    homeViewModel.getWeatherIcon(
                        weatherResponse.weather?.get(0)?.weatherIcon ?: ""
                    )
                ),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
            )
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = weatherResponse.weatherDetails?.temp?.toString() ?: "--",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = homeViewModel.getUnit(units, context),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
            weatherResponse.weather?.get(0)?.fullWeatherDesc
                .let { description ->
                    Text(
                        text = description?.toString() ?: "",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
        }
    }
}

@Composable
fun CircularIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 4.dp
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun HomeLazyColumnItem(
    forecast: Forecast,
    units: String,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xB3424242)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = homeViewModel.getDayName(forecast.dt + 86400),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = homeViewModel.convertTimeStampToDate(forecast.dt),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 16.sp
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(
                        homeViewModel.getWeatherIcon(
                            forecast.weather?.get(0)?.weatherIcon ?: ""
                        )
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                )
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = forecast.weatherDetails?.temp?.toString() ?: "--",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = homeViewModel.getUnit(units, context),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.today_temperature),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = stringResource(R.string.max),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = forecast.weatherDetails?.temp_max.toString(),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = homeViewModel.getUnit(units, context),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = stringResource(R.string.min),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = forecast.weatherDetails?.temp_min.toString(),
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 1.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = homeViewModel.getUnit(units, context),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherCard(
    weatherResponse: WeatherResponse,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current
    val speedUnit = homeViewModel.getAppWindSpeedUnit(context)
    val windSpeed = homeViewModel.calculateWindSpeed(speedUnit, weatherResponse,context)

    Card(
        modifier = Modifier
            .height(200.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xB3666666)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.pressure),
                            contentDescription = stringResource(R.string.pressure),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.pressure),
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = weatherResponse.weatherDetails?.pressure?.toString() ?: "--",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.hpa),
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.humidity),
                            contentDescription = stringResource(R.string.humidity),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.humidity),
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = weatherResponse.weatherDetails?.humidity?.toString() ?: "--",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "%",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.windsp),
                            contentDescription = stringResource(R.string.wind_speed),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.wind_speed),
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = windSpeed.toString(),
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Log.i("TAG", "WeatherCard: $windSpeed")
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = speedUnit,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.cloudper),
                            contentDescription = stringResource(R.string.clouds),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.clouds),
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = weatherResponse.clouds?.cloudPercentage?.toString() ?: "--",
                            fontSize = 20.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "%",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}