package com.example.taq_c.settings.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taq_c.R
import com.example.taq_c.settings.viewModel.SettingViewModel
import com.example.taq_c.utilities.NavigationRoute

@Composable
fun SettingsScreen(navController: NavController) {

    val context = LocalContext.current
    val settingViewModel =
        viewModel<SettingViewModel>()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .height(120.dp)
                .padding(horizontal = 10.dp)
                .padding(top = 12.dp, bottom = 2.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(Color(0xFF424242))
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.language),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 6.dp, start = 12.dp)
                )
                Text(
                    text = stringResource(R.string.language),
                    fontSize = 25.sp,
                    color = Color.White
                )
            }
            LanguageSettings(settingViewModel, context)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .height(120.dp)
                .padding(vertical = 2.dp, horizontal = 10.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(Color(0xFF424242))
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.temperature),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 6.dp, start = 12.dp)
                )
                Text(
                    text = stringResource(R.string.temperature),
                    fontSize = 25.sp,
                    color = Color.White
                )
            }
            TemperatureSettings(context, settingViewModel)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .height(120.dp)
                .padding(vertical = 2.dp, horizontal = 10.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(Color(0xFF424242))
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.windspeed),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 6.dp, start = 12.dp)
                )
                Text(
                    text = stringResource(R.string.speed_unit),
                    fontSize = 25.sp,
                    color = Color.White
                )
            }
            SpeedUnitSetting(context, settingViewModel)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .height(120.dp)
                .padding(vertical = 2.dp, horizontal = 10.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(Color(0xFF424242))
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.location),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 6.dp, start = 12.dp)
                )
                Text(
                    text = stringResource(R.string.location),
                    fontSize = 25.sp,
                    color = Color.White
                )
            }
            LocationSetting(context, settingViewModel, navController)
        }
    }
}

@Composable
private fun LanguageSettings(
    settingViewModel: SettingViewModel,
    context: Context
) {
    settingViewModel
    val langOptions = listOf(
        R.string.arabic,
        R.string.english,
        R.string.defaultLang
    )
    val selectedLanguage = settingViewModel.getSelectedLanguagePreference(context)
    var selectedOption by remember { mutableStateOf(langOptions[selectedLanguage]) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        langOptions.forEach { language ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                RadioButton(
                    selected = (language == selectedOption),
                    onClick = {
                        selectedOption = language
                        when (language) {
                            R.string.arabic -> {
                                settingViewModel.setLanguage(context, "ar")
                                settingViewModel.setAppLanguage(context, "ar")
                            }

                            R.string.english -> {
                                settingViewModel.setLanguage(context)
                                settingViewModel.setAppLanguage(context, "en")
                            }

                            R.string.defaultLang -> {
                                settingViewModel.setLanguage(context)
                                settingViewModel.setAppLanguage(context, "en")
                            }

                            else -> {
                                settingViewModel.setLanguage(context)
                                settingViewModel.setAppLanguage(context, "en")
                            }
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.Black
                    )
                )
                Text(
                    text = stringResource(language),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TemperatureSettings(
    context: Context,
    settingViewModel: SettingViewModel
) {
    val tempOptions = listOf(
        R.string.celsius,
        R.string.kelvin,
        R.string.fahrenheit,
    )
    val selectedTemp = settingViewModel.getSelectedTempPreference(context)
    var selectedOption by remember { mutableStateOf(tempOptions[selectedTemp]) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tempOptions.forEach { temp ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                RadioButton(
                    selected = (temp == selectedOption),
                    onClick = {
                        selectedOption = temp
                        when (temp) {
                            R.string.celsius -> {
                                settingViewModel.setTemperatureUnit(context, "metric")
                            }

                            R.string.kelvin -> {
                                settingViewModel.setTemperatureUnit(context, "kelvin")
                            }

                            R.string.fahrenheit -> {
                                settingViewModel.setTemperatureUnit(context, "imperial")
                            }

                            else -> {
                                settingViewModel.setTemperatureUnit(context)
                            }
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.Black
                    )
                )
                Text(
                    text = stringResource(temp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SpeedUnitSetting(
    context: Context,
    settingViewModel: SettingViewModel
) {
    val speedOptions = listOf(
        R.string.km_h,
        R.string.mph
    )
    val selectedSpeed = settingViewModel.getSelectedWindSpeedUnitPreference(context)
    var selectedOption by remember { mutableStateOf(speedOptions[selectedSpeed]) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        speedOptions.forEach { speed ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                RadioButton(
                    selected = (speed == selectedOption),
                    onClick = {
                        selectedOption = speed
                        when (speed) {
                            R.string.km_h -> {
                                settingViewModel.setWindSpeedUnit(context, "km/h")
                            }

                            R.string.mph -> {
                                settingViewModel.setWindSpeedUnit(context, "mph")
                            }

                            else -> {
                                settingViewModel.setWindSpeedUnit(context)
                            }
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.Black
                    )
                )
                Text(
                    text = stringResource(speed),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun LocationSetting(
    context: Context,
    settingViewModel: SettingViewModel,
    navController: NavController
) {
    val speedOptions = listOf(
        R.string.gps,
        R.string.map
    )
    val selectedLocation = settingViewModel.getSelectedLocationPreference(context)
    var selectedOption by remember { mutableStateOf(speedOptions[selectedLocation]) }
    var showMap by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        speedOptions.forEach { location ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp)
            ) {
                RadioButton(
                    selected = (location == selectedOption),
                    onClick = {
                        selectedOption = location
                        when (location) {
                            R.string.gps -> {
                                settingViewModel.setLocationType(context, "GPS")
                            }

                            R.string.map -> {
                                settingViewModel.setLocationType(context, "Map")
                                navController.navigate(NavigationRoute.MapScreen(true, false))
                            }

                            else -> {
                                settingViewModel.setLocationType(context)
                            }
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.Black
                    )
                )
                Text(
                    text = stringResource(location),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}
