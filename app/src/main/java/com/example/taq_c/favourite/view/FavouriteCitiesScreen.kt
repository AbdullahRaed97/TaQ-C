package com.example.taq_c.favourite.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.taq_c.home.view.BottomActionBar

@Composable
fun FavoriteCityScreen(){
    Scaffold(
        bottomBar = {
            BottomActionBar()
        }, containerColor = Color(0xFF0c1a4d)
    ){
            contentPadding->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(contentPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Home Screen",
                fontSize = 30.sp ,
                color = Color.White)
        }

    }
}