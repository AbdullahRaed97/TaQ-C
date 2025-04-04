package com.example.taq_c.settings.view

import android.content.Context
import androidx.activity.ComponentActivity
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
                horizontalArrangement = Arrangement.Start
            , verticalAlignment = Alignment.CenterVertically
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
            TemperatureSettings(context , settingViewModel)
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
                horizontalArrangement = Arrangement.Start
                , verticalAlignment = Alignment.CenterVertically
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
            SpeedUnitSetting(context,settingViewModel)
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
                horizontalArrangement = Arrangement.Start
                , verticalAlignment = Alignment.CenterVertically) {
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
            LocationSetting(context,settingViewModel,navController)
        }
    }
}

@Composable
private fun LanguageSettings(
    settingViewModel: SettingViewModel,
    context: Context
) {
    val langOptions = listOf(
        stringResource(R.string.arabic),
        stringResource(R.string.english),
        stringResource(R.string.defaultLang)
    )
    val selectedLanguage = settingViewModel.getTheSelectedLanguage(context)
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
                            "Arabic" -> {
                                settingViewModel.setLanguage(context,"ar")
                                settingViewModel.setTheSelectedLanguage(context, language)
                                (context as ComponentActivity).recreate()
                            }

                            "English" -> {
                                settingViewModel.setLanguage(context)
                                settingViewModel.setTheSelectedLanguage(context, language)
                                (context as ComponentActivity).recreate()
                            }

                            "Default" -> {
                                settingViewModel.setLanguage(context)
                                settingViewModel.setTheSelectedLanguage(context, language)
                                (context as ComponentActivity).recreate()
                            }

                            else -> {
                                settingViewModel.setLanguage(context)
                                settingViewModel.setTheSelectedLanguage(context, language)
                                (context as ComponentActivity).recreate()
                            }
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.Black                    )
                )
                Text(
                    text = language,
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
){
    val tempOptions = listOf(
        stringResource(R.string.celsius),
        stringResource(R.string.kelvin),
        stringResource(R.string.fahrenheit)
    )
    val selectedTemp = settingViewModel.getTheSelectedTemperature(context)
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
                            "Celsius" -> {
                                settingViewModel.setTemperatureUnit(context,"metric")
                                settingViewModel.setTheSelectedTemperature(context, temp)
                            }

                            "Kelvin" -> {
                                settingViewModel.setTemperatureUnit(context,"kelvin")
                                settingViewModel.setTheSelectedTemperature(context, temp)
                            }

                            "Fahrenheit" -> {
                                settingViewModel.setTemperatureUnit(context,"imperial")
                                settingViewModel.setTheSelectedTemperature(context, temp)
                            }

                            else -> {
                                settingViewModel.setTemperatureUnit(context)
                                settingViewModel.setTheSelectedTemperature(context, temp)
                            }
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.Black
                    )
                )
                Text(
                    text = temp,
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
){
    val speedOptions = listOf(
        stringResource(R.string.km_h),
        stringResource(R.string.mph)
    )
    val selectedSpeed = settingViewModel.getTheSelectedSpeedUnit(context)
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
                            "km/h" -> {
                                settingViewModel.setWindSpeedUnit(context,speed)
                                settingViewModel.setTheSelectedSpeedUnit(context, speed)
                            }

                            "mph" -> {
                                settingViewModel.setWindSpeedUnit(context,speed)
                                settingViewModel.setTheSelectedSpeedUnit(context, speed)
                            }
                            else -> {
                                settingViewModel.setWindSpeedUnit(context)
                                settingViewModel.setTheSelectedSpeedUnit(context, speed)
                            }
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.Black
                    )
                )
                Text(
                    text = speed,
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
){
    val speedOptions = listOf(
        stringResource(R.string.gps),
        stringResource(R.string.map)
    )
    val selectedLocation = settingViewModel.getTheSelectedLocationType(context)
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
                            "GPS" -> {
                                settingViewModel.setLocationType(context,location)
                                settingViewModel.setTheSelectedLocationType(context, location)
                            }

                            "Map" -> {
                                settingViewModel.setLocationType(context,location)
                                settingViewModel.setTheSelectedLocationType(context, location)
                                navController.navigate(NavigationRoute.MapScreen(true,false))
                            }
                            else -> {
                                settingViewModel.setLocationType(context)
                                settingViewModel.setTheSelectedLocationType(context, location)
                            }
                        }
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = Color.Black
                    )
                )
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
        }
    }
}
