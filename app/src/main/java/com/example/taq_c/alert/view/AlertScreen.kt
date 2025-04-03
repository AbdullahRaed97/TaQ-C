package com.example.taq_c.alert.view

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taq_c.alert.viewModel.AlertFactory
import com.example.taq_c.alert.viewModel.AlertViewModel
import com.example.taq_c.data.db.WeatherDatabase
import com.example.taq_c.data.local.WeatherLocalDataSource
import com.example.taq_c.data.model.Alert
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.remote.RetrofitHelper
import com.example.taq_c.data.remote.WeatherRemoteDataSource
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.home.view.CircularIndicator
import com.example.taq_c.utilities.NavigationRoute

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AlertScreen(
    floatingActionButtonAction : MutableState<(()->Unit)?>
    ,navController: NavController
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
    val alertResponse = alertViewModel.alertResponse.collectAsStateWithLifecycle().value
    var showDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        alertViewModel.getAllAlert()
    }

    floatingActionButtonAction.value ={
     if(alertViewModel.checkNotificationOpened(context)){
     navController.navigate(NavigationRoute.SetAlertScreen(0.0,0.0)){
         launchSingleTop=true
         }
     }else{
         showDialog = true
     }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(alertResponse){
            is Response.Failure ->{

            }
            Response.Loading -> {
                CircularIndicator()
            }
            is Response.Success ->{
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (alertResponse.data != null) {
                        itemsIndexed(alertResponse.data) { index, item ->
                            AlertItem(item,alertViewModel)
                        }
                    }
                }
            }
        }
    }
    if(showDialog){
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Allow Notification") },
            text = { Text("Please allow notification for this app to send Alerts") },
            confirmButton = {
                TextButton(
                    onClick = {
                        alertViewModel.allowNotification(context)
                        showDialog = false
                    }
                ) {
                    Text("Settings")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AlertItem(alert: Alert, alertViewModel: AlertViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Log.i("TAG", "AlertItem: ${alert.timeStamp}")
    Card(
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF424242)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column (
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = alertViewModel.getCountryName(alert.city.country),
                    fontSize = 22.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = alertViewModel.convertTimeStampToDate(alert.timeStamp?:0),
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = alert.city.name.toString(),
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete alert",
                tint = Color.White.copy(alpha = 0.8f),
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        showDialog = true
                    }
                    .padding(8.dp)
            )
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        "Delete Alert",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to delete this Alert",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            alertViewModel.deleteAlert(context, alert)
                        }
                    ) {
                        Text(
                            "Delete",
                            color = Color.Red
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = Color(0xFF424242),
                titleContentColor = Color.White,
                textContentColor = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

