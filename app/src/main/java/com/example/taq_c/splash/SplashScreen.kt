package com.example.taq_c.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
fun SplashScreen(
    navController: NavController,
    lat: Double,
    lon: Double,
    showNavigationBar: MutableState<Boolean>
) {
    val composition by
    rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash))
    val scale = remember {
        Animatable(0f)
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1f,
        isPlaying = true
    )

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 0.3f,
            animationSpec = tween(
                durationMillis = 500,
                easing = {
                    OvershootInterpolator(2f).getInterpolation(it)
                }
            )
        )
        delay(3000)
        showNavigationBar.value = true
        navController.navigate(NavigationRoute.HomeScreen(lat, lon)) {
            popUpTo(navController.graph.startDestinationId) {
                inclusive = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .size(500.dp)
                .scale(scale.value),
            progress = { progress },
        )
    }
}
