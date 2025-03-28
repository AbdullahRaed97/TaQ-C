package com.example.taq_c

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.taq_c.alarm.AlarmScreen
import com.example.taq_c.favourite.view.FavoriteCityScreen
import com.example.taq_c.favourite.view.MapScreen
import com.example.taq_c.home.view.BottomNavigationItem
import com.example.taq_c.home.view.HomeScreen
import com.example.taq_c.settings.view.SettingsScreen
import com.example.taq_c.utilities.NavigationRoute
import java.util.Locale


class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context?) {
        //get app language
        val sharedPreferences = newBase?.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val appLanguage = sharedPreferences?.getString("Language","en")?:"en"
        //create new Locale and make it default
        val locale = Locale(appLanguage)
        Locale.setDefault(locale)
        //get the configuration of the app
        val resources = newBase?.resources
        val config = resources?.configuration
        //adjust the configuration
        config?.setLocale(locale)
        config?.setLayoutDirection(locale)
        //send the new context with the new configuration
        if(config!=null) {
            super.attachBaseContext(newBase.createConfigurationContext(config))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()
            Scaffold(
                bottomBar = {
                    BottomActionBar(navController)
                }, containerColor = Color(0xFF0c1a4d),
            ) { contentPadding ->
                NavHost(
                    navController = navController,
                    startDestination = NavigationRoute.HomeScreen(31.0,30.0),
                    modifier = Modifier.padding(contentPadding)
                ) {
                    composable<NavigationRoute.HomeScreen> {
                        val receivedObject = it.toRoute<NavigationRoute.HomeScreen>()
                        val lat = receivedObject.lat
                        val lon = receivedObject.lon
                        HomeScreen(lat,lon)
                    }
                    composable<NavigationRoute.SettingScreen> {
                        SettingsScreen(navController)
                    }
                    composable<NavigationRoute.FavoriteScreen> {
                        FavoriteCityScreen(navController)
                    }
                    composable<NavigationRoute.AlarmScreen> {
                        AlarmScreen()
                    }
                    composable<NavigationRoute.MapScreen>{
                        val receivedObject = it.toRoute<NavigationRoute.MapScreen>()
                        val fromSetting = receivedObject.fromSetting
                        MapScreen(fromSetting,navController)
                    }
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        //check permission
        if(Location.checkPermission(this)){
            //check if the location is enabled
            if(Location.locationEnabled(this)){
                Location.getFreshLocation(this)
            }else{
                //enable the Location service
                Location.enableLocationService(this)
            }
        }else{
            //No permission supported so request permission
            ActivityCompat.requestPermissions(this ,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION
                    ,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                Location.REQUEST_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if(requestCode==Location.REQUEST_CODE){
            if(grantResults.get(0)== PackageManager.PERMISSION_GRANTED || grantResults.get(1) == PackageManager.PERMISSION_GRANTED){
                if(Location.locationEnabled(this)){
                    Location.getFreshLocation(this)
                }else{
                    Location.enableLocationService(this)
                }
            }else{
                ActivityCompat.requestPermissions(this ,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION
                        ,android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    Location.REQUEST_CODE
                )
            }

        }
    }

}

@Composable
fun BottomActionBar(navController: NavController){
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home,
            hasNews = false,
            navigationAction = {navController.navigate(NavigationRoute.HomeScreen(31.0,30.0))}
        ),
        BottomNavigationItem(
            title = "Favorite",
            selectedIcon = Icons.Filled.Favorite,
            unSelectedIcon = Icons.Outlined.Favorite,
            hasNews = false,
            navigationAction = {navController.navigate(NavigationRoute.FavoriteScreen)}
        ),
        BottomNavigationItem(
            title = "Alarm",
            selectedIcon = Icons.Filled.Notifications,
            unSelectedIcon = Icons.Outlined.Notifications,
            hasNews = false,
            navigationAction = {navController.navigate(NavigationRoute.AlarmScreen)}
        ),
        BottomNavigationItem(
            title = "Setting",
            selectedIcon = Icons.Filled.Settings,
            unSelectedIcon = Icons.Outlined.Settings,
            hasNews = false,
            navigationAction = {navController.navigate(NavigationRoute.SettingScreen)}
        )
    )
    var selectedItemIndex by remember {
        mutableStateOf(0)
    }
    NavigationBar(
        containerColor = Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .padding(1.dp)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedItemIndex == index,
                onClick = {
                    selectedItemIndex = index
                    item.navigationAction()
                    Log.i("TAG", "BottomActionBar: Clicked")
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
                            contentDescription = item.title
                        )
                    }
                },
                label = {
                    Text(text = item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Gray,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color(0xB58696E8)
                )
            )
        }
    }
}