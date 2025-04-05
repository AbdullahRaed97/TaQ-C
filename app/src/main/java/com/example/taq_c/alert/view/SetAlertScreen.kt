package com.example.taq_c.alert.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taq_c.R
import com.example.taq_c.alert.viewModel.AlertFactory
import com.example.taq_c.alert.viewModel.AlertViewModel
import com.example.taq_c.data.db.WeatherDatabase
import com.example.taq_c.data.local.WeatherLocalDataSource
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.remote.RetrofitHelper
import com.example.taq_c.data.remote.WeatherRemoteDataSource
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.home.view.CircularIndicator
import com.example.taq_c.utilities.NavigationRoute.AlertScreen
import com.example.taq_c.utilities.NavigationRoute.MapScreen
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetAlertScreen(
    navController: NavController,
    lat: Double,
    lon: Double,
    snackBarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val weatherRepository = WeatherRepository.
    getInstance(WeatherLocalDataSource
        .getInstance(WeatherDatabase
            .getInstance(context).getWeatherDao(),WeatherDatabase
            .getInstance(context).getAlertDao()),
        WeatherRemoteDataSource
            .getInstance(RetrofitHelper.weatherService)
    )
    val alertViewModel = viewModel<AlertViewModel>(factory = AlertFactory(weatherRepository))
    val forecastResponse = alertViewModel.forecastResponse.collectAsStateWithLifecycle().value
    var showTimePicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    val message = alertViewModel.message.collectAsStateWithLifecycle(initialValue = null).value
    var timeStamp: MutableState<Long> = remember { mutableStateOf(0) }
    val currentTime = Calendar.getInstance()
    var showValidation by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = false,
    )
    LaunchedEffect(message) {
        if(message != null){
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    LaunchedEffect(lat) {
        alertViewModel.getForeCastResponse(lat, lon)
    }
    when (forecastResponse) {
        is Response.Success -> {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
                    .padding(vertical = 150.dp, horizontal = 20.dp)
                    .clip(RoundedCornerShape(10.dp)),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(Color.White),
                border = BorderStroke(5.dp, color = Color.Black)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                       painter =  painterResource(R.drawable.alarm),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)

                    )
                    Button(
                        onClick = {
                            navController.navigate(MapScreen(false, true))
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.global),
                            contentDescription = null,
                            modifier = Modifier.padding(5.dp)
                        )
                        Text(
                            text = stringResource(R.string.set_your_destination),
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = {
                            showTimePicker = true
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.timer),
                            contentDescription = null,
                            modifier = Modifier.padding(5.dp)
                        )
                        Text(
                            text = stringResource(R.string.pick_up_your_time),
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                    Button(
                        onClick = {
                            showDatePicker = true
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.date),
                            contentDescription = null,
                            modifier = Modifier.padding(5.dp)
                        )
                        Text(
                            text = stringResource(R.string.pick_up_your_date),
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = {
                            if((lat==0.0)&&(lon==0.0)){
                                showValidation = true
                            }else {
                                alertViewModel.requestAlert(
                                    context,
                                    forecastResponse.data.city ?: City(),
                                    timePickerState.hour,
                                    timePickerState.minute,
                                    timeStamp.value
                                )
                                navController.navigate(AlertScreen)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.confirm),
                            contentDescription = null,
                            modifier = Modifier.padding(5.dp)
                        )
                        Text(
                            text = stringResource(R.string.confirm_your_alert),
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        is Response.Failure -> {}
        Response.Loading -> CircularIndicator()
    }
    if (showTimePicker)
        ShowTimePicker(
            timePickerState,
            {
                showTimePicker = false
            }) {
            showTimePicker = false
        }
    if (showDatePicker)
        ShowDatePicker({
            timeStamp.value = it ?: 0
            showDatePicker = false
        }) {
            showDatePicker = false
        }
    if(showValidation){
        AlertDialog(
            onDismissRequest = { showValidation = false },
            title = { Text(stringResource(R.string.alert_validation)) },
            text = { Text(stringResource(R.string.please_select_your_destination)) },
            confirmButton = {
                TextButton(
                    onClick = { showValidation = false }
                ) {
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showValidation = false }
                ) {
                    Text(stringResource(R.string.cancel),
                        color = Color.Red
                    )

                }
            }
            ,containerColor = Color(0xFF424242),
            titleContentColor = Color.White,
            textContentColor = Color.White.copy(alpha = 0.8f)
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTimePicker(
    timePickerState: TimePickerState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimePicker(
            state = timePickerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .background(Color.White)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .width(150.dp)
                    .padding(end = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.confirm),
                    color = Color.White
                )
            }
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .width(150.dp)
            ) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShowDatePicker(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = object:SelectableDates{
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis>= System.currentTimeMillis()
            }
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}