package com.example.taq_c.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.taq_c.R
import com.example.taq_c.utilities.NavigationRoute
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController,lat: Double , lon: Double , showNavigationBar : MutableState<Boolean>){
    val composition by
    rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1f,
        isPlaying = true
    )

    LaunchedEffect(Unit) {
        delay(3000)
        showNavigationBar.value = true
        navController.navigate(NavigationRoute.HomeScreen(lat,lon)) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ){
        LottieAnimation(
            composition = composition,
            modifier = Modifier.size(500.dp),
            progress = {progress}
        )
    }
}
