package com.example.taq_c.favourite.view

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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.remote.RetrofitHelper
import com.example.taq_c.data.remote.WeatherRemoteDataSource
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.favourite.viewModel.FavouriteFactory
import com.example.taq_c.favourite.viewModel.FavouriteViewModel
import com.example.taq_c.home.view.CircularIndicator
import com.example.taq_c.utilities.NavigationRoute


@Composable
fun FavoriteCityScreen(
    navController: NavController,
    floatingActionButtonAction: MutableState<(() -> Unit)?>,
    snackBarHostState: SnackbarHostState
) {
    var isClicked by remember { mutableStateOf(false) }
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
    val favViewModel = viewModel<FavouriteViewModel>(factory = FavouriteFactory(weatherRepository))
    val message = favViewModel.message.collectAsStateWithLifecycle(initialValue = null).value

    LaunchedEffect(message) {
        if (message != null) {
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }
    LaunchedEffect(Unit) {
        favViewModel.getAllFavCities()
    }

    val favCityResponse = favViewModel.favCitiesResponse.collectAsStateWithLifecycle().value
    val snackBarHostState = remember { SnackbarHostState() }

    floatingActionButtonAction.value = {
        navController.navigate(NavigationRoute.MapScreen(false, false))
    }

    Column(
        modifier = Modifier.padding(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (favCityResponse) {
            is Response.Failure -> {}
            is Response.Loading -> {
                CircularIndicator()
            }

            is Response.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (favCityResponse.data != null) {
                        itemsIndexed(favCityResponse.data) { index, item ->
                            FavCityItem(navController, item, favViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavCityItem(navController: NavController, city: City, favViewModel: FavouriteViewModel) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                navController.navigate(
                    NavigationRoute.HomeScreen(
                        city.coord?.lat ?: 0.0,
                        city.coord?.lon ?: 0.0
                    )
                ) {
                    popUpTo(NavigationRoute.FavoriteScreen) {
                        inclusive = true
                    }
                }
            },
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
            Text(
                text = favViewModel.getCountryName(city.country),
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = city.name.toString(),
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
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
                        stringResource(R.string.delete_city),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Text(
                        stringResource(R.string.are_you_sure_you_want_to_delete_this_city),
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDialog = false
                            favViewModel.deleteFavCity(city)
                        }
                    ) {
                        Text(
                            stringResource(R.string.delete),
                            color = Color.Red
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                },
                containerColor = Color(0xFF424242),
                titleContentColor = Color.White,
                textContentColor = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
