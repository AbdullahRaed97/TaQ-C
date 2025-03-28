package com.example.taq_c.favourite.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.taq_c.data.model.City
import com.example.taq_c.data.model.Response
import com.example.taq_c.data.repository.WeatherRepository
import com.example.taq_c.favourite.viewModel.FavouriteFactory
import com.example.taq_c.favourite.viewModel.FavouriteViewModel
import com.example.taq_c.home.view.CircularIndicator
import com.example.taq_c.utilities.NavigationRoute


@Composable
fun FavoriteCityScreen(navController: NavController) {
    var isClicked by remember { mutableStateOf(false) }
    val weatherRepository = WeatherRepository.getInstance(LocalContext.current)
    val favViewModel = viewModel<FavouriteViewModel>(factory = FavouriteFactory(weatherRepository))
    LaunchedEffect(Unit) {
        favViewModel.getAllFavCities()
    }
    val favCityResponse = favViewModel.favCitiesResponse.collectAsStateWithLifecycle().value
    val snackBarHostState = remember { SnackbarHostState() }
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(NavigationRoute.MapScreen(false))
            },
                contentColor = Color.White,
                containerColor =  Color.DarkGray,
                modifier = Modifier.clickable{
                    isClicked = !isClicked
                }.scale(if(isClicked)1.2f else 1f)
                .animateContentSize()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        },
        containerColor = Color(0xFF0c1a4d)
    )
     {contentPadding ->
        Column(modifier = Modifier.padding(contentPadding)) {
            when (favCityResponse) {

                is Response.Failure -> {
                    LaunchedEffect(favCityResponse) {
                        snackBarHostState.showSnackbar(
                            message = favCityResponse.exception.message.toString(),
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is Response.Loading -> {
                    CircularIndicator()
                }

                is Response.Success -> {
                    LazyColumn(modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center) {
                        if(favCityResponse.data!=null) {
                            itemsIndexed(favCityResponse.data) {index,item ->
                                    FavCityItem(navController,item,favViewModel)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun FavCityItem(navController: NavController,city: City,favViewModel: FavouriteViewModel){
    var showDialog by remember { mutableStateOf(false) }
    Card (
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth()
            .padding(15.dp)
            .clickable{
                navController.navigate(NavigationRoute.HomeScreen(
                    city.coord?.lat?:0.0,city.coord?.lon?:0.0
                )){
                    popUpTo(NavigationRoute.FavoriteScreen){
                        inclusive=true
                    }
                }
            },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(Color.Gray)){
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically){
            Text(
                text = favViewModel.getCountryName(city.country),
                fontSize = 25.sp,
                color = Color.White
            )
            Text(
                text = city.name.toString()
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.clickable{
                    showDialog = true
                }.scale( 1.2f ,1f)
            )
        }
        if (showDialog){
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Delete City") },
                text = { Text("Are you sure you want to delete this city") },
                confirmButton = {
                    TextButton(
                        onClick = { showDialog = false
                            favViewModel.deleteFavCity(city)
                        }
                    ) {
                        Text("Delete")
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
}
