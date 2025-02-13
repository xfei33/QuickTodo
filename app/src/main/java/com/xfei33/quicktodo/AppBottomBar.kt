package com.xfei33.quicktodo

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun AppBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_todo), contentDescription = "待办") },
            label = { Text("待办") },
            selected = currentRoute == "todo",
            onClick = { navController.navigate("todo") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_focus), contentDescription = "专注") },
            label = { Text("专注") },
            selected = currentRoute == "focus",
            onClick = { navController.navigate("focus") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_message), contentDescription = "消息") },
            label = { Text("消息") },
            selected = currentRoute == "message",
            onClick = { navController.navigate("message") }
        )
        NavigationBarItem(
            icon = { Icon(painterResource(id = R.drawable.ic_profile), contentDescription = "我的") },
            label = { Text("我的") },
            selected = currentRoute == "profile",
            onClick = { navController.navigate("profile") }
        )
    }
}
