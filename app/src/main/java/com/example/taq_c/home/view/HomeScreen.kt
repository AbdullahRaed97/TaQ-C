package com.example.taq_c.home.view


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavigationItem(
    val title:String,
    val selectedIcon : ImageVector,
    val unSelectedIcon: ImageVector,
    val hasNews : Boolean,
)


@Preview(showSystemUi = false)
@Composable
fun BottomActionBar(){
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unSelectedIcon = Icons.Outlined.Home,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "Favorite",
            selectedIcon = Icons.Filled.Favorite,
            unSelectedIcon = Icons.Outlined.Favorite,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "Alarm",
            selectedIcon = Icons.Filled.Notifications,
            unSelectedIcon = Icons.Outlined.Notifications,
            hasNews = false
        ),
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Settings,
            unSelectedIcon = Icons.Outlined.Settings,
            hasNews = false
        )
    )
    var selectedItemIndex by remember {
        mutableStateOf(0)
    }
        NavigationBar(
            containerColor = Color.White,
            modifier = Modifier.clip(RoundedCornerShape(20.dp))
                .padding(1.dp)
        ) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedItemIndex == index,
                    onClick = {
                        selectedItemIndex = index
                        //Navigate
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
                    }
                )
            }
        }
}


@Preview(showSystemUi = true)
@Composable
fun HomeScreen(){
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



