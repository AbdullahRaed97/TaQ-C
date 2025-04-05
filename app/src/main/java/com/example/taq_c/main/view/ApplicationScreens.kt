package com.example.taq_c.main.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.taq_c.R
import com.example.taq_c.alert.view.AlertScreen
import com.example.taq_c.alert.view.SetAlertScreen
import com.example.taq_c.favourite.view.FavoriteCityScreen
import com.example.taq_c.favourite.view.MapScreen
import com.example.taq_c.home.view.BottomNavigationItem
import com.example.taq_c.home.view.HomeScreen
import com.example.taq_c.settings.view.SettingsScreen
import com.example.taq_c.splash.SplashScreen
import com.example.taq_c.utilities.LocationHelper
import com.example.taq_c.utilities.NavigationRoute

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ApplicationScreens(isNetworkAvailable: Boolean?) {
    val context = LocalContext.current
    val floatingActionButtonAction: MutableState<(() -> Unit)?> =
        remember { mutableStateOf(null) }
    val dayState = remember { mutableStateOf("01d") }
    val showNavigationBar: MutableState<Boolean> = remember { mutableStateOf(false) }
    val snackBarState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    LaunchedEffect(isNetworkAvailable) {
        when (isNetworkAvailable) {
            true -> {
                snackBarState.showSnackbar(
                    message = context.getString(R.string.network_available),
                    duration = SnackbarDuration.Short
                )
            }

            false -> {
                snackBarState.showSnackbar(
                    message = context.getString(R.string.no_internet_connection),
                    duration = SnackbarDuration.Short
                )
            }

            null -> {}
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(
                when (dayState.value) {
                    "01d" -> R.drawable.clear_skybg
                    "01n" -> R.drawable.nclear_skybg
                    "02d", "03d", "04d" -> R.drawable.dfew_cloud
                    "02n", "03n", "04n" -> R.drawable.nfew_cloudbg
                    "09n", "10n" -> R.drawable.nrainbg
                    "09d", "10d" -> R.drawable.drainbg
                    "11d", "11n" -> R.drawable.thunderbg
                    "13d" -> R.drawable.dsnowbg
                    "13n" -> R.drawable.nsnowbg
                    "50d", "50n" -> R.drawable.mistbg
                    else -> R.drawable.clear_skybg
                }
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Scaffold(
            bottomBar = {
                if (showNavigationBar.value) {
                    BottomActionBar(navController)
                }
            },
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(snackBarState)
            },
            floatingActionButton = {
                if (floatingActionButtonAction.value != null) {
                    FloatingActionButton(
                        onClick = {
                            floatingActionButtonAction.value?.invoke()
                        },
                        contentColor = Color.White,
                        containerColor = Color.DarkGray,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    }
                }

            }
        ) { contentPadding ->
            NavHost(
                navController = navController,
                startDestination = NavigationRoute.SplashScreen,
                modifier = Modifier
                    .padding(contentPadding)
                    .fillMaxSize(),
            ) {
                composable<NavigationRoute.SplashScreen> {
                    SplashScreen(
                        navController,
                        LocationHelper.getLatitude(context),
                        LocationHelper.getLongitude(context),
                        showNavigationBar
                    )
                }
                composable<NavigationRoute.HomeScreen> {
                    val receivedObject = it.toRoute<NavigationRoute.HomeScreen>()
                    val lat = receivedObject.lat
                    val lon = receivedObject.lon
                    floatingActionButtonAction.value = null
                    showNavigationBar.value = true
                    HomeScreen(lat, lon, isNetworkAvailable, snackBarState, dayState = dayState)
                }
                composable<NavigationRoute.SettingScreen> {
                    floatingActionButtonAction.value = null
                    showNavigationBar.value = true
                    SettingsScreen(navController)
                }
                composable<NavigationRoute.FavoriteScreen> {
                    showNavigationBar.value = true
                    FavoriteCityScreen(navController, floatingActionButtonAction, snackBarState)
                }
                composable<NavigationRoute.AlertScreen> {
                    showNavigationBar.value = true
                    AlertScreen(floatingActionButtonAction, navController, snackBarState)
                }
                composable<NavigationRoute.MapScreen> {
                    val receivedObject = it.toRoute<NavigationRoute.MapScreen>()
                    val fromSetting = receivedObject.fromSetting
                    val fromAlert = receivedObject.fromAlert
                    floatingActionButtonAction.value = null
                    showNavigationBar.value = true
                    MapScreen(fromSetting, fromAlert, navController, snackBarState)
                }
                composable<NavigationRoute.SetAlertScreen> {
                    val receivedObject = it.toRoute<NavigationRoute.SetAlertScreen>()
                    val lat = receivedObject.lat
                    val lon = receivedObject.lon
                    showNavigationBar.value = true
                    SetAlertScreen(navController, lat, lon, snackBarState)
                }
            }
        }
    }
}

@Composable
fun BottomActionBar(navController: NavController) {
    val context = LocalContext.current
    val items = listOf(
        BottomNavigationItem(
            title = stringResource(R.string.home),
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home,
            hasNews = false,
            navigationAction = {
                navController.navigate(
                    NavigationRoute.HomeScreen
                        (
                        LocationHelper.getLatitude(context), LocationHelper.getLongitude(context)
                    )
                ) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        ),
        BottomNavigationItem(
            title = stringResource(R.string.favorite),
            selectedIcon = Icons.Filled.Favorite,
            unSelectedIcon = Icons.Outlined.Favorite,
            hasNews = false,
            navigationAction = {
                navController.navigate(NavigationRoute.FavoriteScreen) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        ),
        BottomNavigationItem(
            title = stringResource(R.string.alarm),
            selectedIcon = Icons.Filled.Notifications,
            unSelectedIcon = Icons.Outlined.Notifications,
            hasNews = false,
            navigationAction = {
                navController.navigate(NavigationRoute.AlertScreen) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        ),
        BottomNavigationItem(
            title = stringResource(R.string.setting),
            selectedIcon = Icons.Filled.Settings,
            unSelectedIcon = Icons.Outlined.Settings,
            hasNews = false,
            navigationAction = {
                navController.navigate(NavigationRoute.SettingScreen) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            }
        )
    )
    var selectedItemIndex by remember {
        mutableStateOf(0)
    }
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .padding(1.dp),
        contentColor = Color.Gray
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    item.navigationAction()
                },
                icon = {
                    BadgedBox(
                        badge = {}
                    ) {
                        Icon(
                            imageVector = if (index == selectedItemIndex) {
                                item.selectedIcon
                            } else {
                                item.unSelectedIcon
                            },
                            contentDescription = item.title,
                            tint = Color.Gray
                        )
                    }
                },
                label = {
                    Text(text = item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Gray,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.White,
                )
            )
        }
    }
}