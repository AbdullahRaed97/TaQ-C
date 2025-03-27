package com.example.taq_c.settings.view

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.material3.Icon
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
import com.example.taq_c.R
import com.example.taq_c.settings.viewModel.SettingFactory
import com.example.taq_c.settings.viewModel.SettingViewModel


@Composable
fun SettingsScreen() {

    val context = LocalContext.current
    val settingViewModel = viewModel<SettingViewModel>(factory = SettingFactory(LocalContext.current))
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .height(120.dp)
                .padding(vertical = 15.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(Color.Gray)
        ) {
            Row(horizontalArrangement = Arrangement.Start) {
                Icon(
                    painter = painterResource(R.drawable.language),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 6.dp, start = 6.dp),
                    tint = Color.White
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
                .padding(vertical = 15.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(Color.Gray)
        ) {
            Row(horizontalArrangement = Arrangement.Start) {
                Icon(
                    painter = painterResource(R.drawable.temperature),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 6.dp, start = 6.dp),
                    tint = Color.White
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
                .padding(vertical = 15.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(Color.Gray)
        ) {
            Row(horizontalArrangement = Arrangement.Start) {
                Icon(
                    painter = painterResource(R.drawable.windspeed),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .padding(end = 6.dp, start = 6.dp),
                    tint = Color.White
                )
                Text(
                    text = stringResource(R.string.speed_unit),
                    fontSize = 25.sp,
                    color = Color.White
                )
            }
            //SpeedUnitSetting
        }
    }
}

@Composable
private fun LanguageSettings(
    settingViewModel: SettingViewModel,
    context: Context
) {
    val langOptions = listOf("Arabic", "English", "Default")
    val selectedLanguage = settingViewModel.getTheSelectedLanguage(context)
    var selectedLang by remember { mutableStateOf(langOptions[selectedLanguage]) }

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
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                RadioButton(
                    selected = (language == selectedLang),
                    onClick = {
                        selectedLang = language
                        when (language) {
                            "Arabic" -> {
                                settingViewModel.setLanguage("ar")
                                settingViewModel.setTheSelectedLanguage(context, language)
                                (context as ComponentActivity).recreate()
                            }

                            "English" -> {
                                settingViewModel.setLanguage()
                                settingViewModel.setTheSelectedLanguage(context, language)
                                (context as ComponentActivity).recreate()
                            }

                            "Default" -> {
                                settingViewModel.setLanguage()
                                settingViewModel.setTheSelectedLanguage(context, language)
                                (context as ComponentActivity).recreate()
                            }

                            else -> {
                                settingViewModel.setLanguage()
                                settingViewModel.setTheSelectedLanguage(context, language)
                                (context as ComponentActivity).recreate()
                            }
                        }
                        Log.i("TAG", "SettingsScreen: ${settingViewModel.getLanguage()}")
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = language,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp)
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
    val tempOptions = listOf("Celsius", "Kelvin", "Fahrenheit")
    val selectedTemp = settingViewModel.getTheSelectedTemperature(context)
    var selectedLang by remember { mutableStateOf(tempOptions[selectedTemp]) }
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
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                RadioButton(
                    selected = (temp == selectedLang),
                    onClick = {
                        selectedLang = temp
                        when (temp) {
                            "Celsius" -> {
                                settingViewModel.setTemperatureUnit("metric")
                                settingViewModel.setTheSelectedTemperature(context, temp)
                            }

                            "Kelvin" -> {
                                settingViewModel.setTemperatureUnit("kelvin")
                                settingViewModel.setTheSelectedTemperature(context, temp)
                            }

                            "Fahrenheit" -> {
                                settingViewModel.setTemperatureUnit("imperial")
                                settingViewModel.setTheSelectedTemperature(context, temp)
                            }

                            else -> {
                                settingViewModel.setTemperatureUnit()
                                settingViewModel.setTheSelectedTemperature(context, temp)
                            }
                        }
                        Log.i("TAG", "SettingsScreen: ${settingViewModel.getTemperatureUnit()}")
                    },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.Red,
                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = temp,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}


