package com.xfei33.quicktodo.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xfei33.quicktodo.components.AppBottomBar
import com.xfei33.quicktodo.ui.focus.FocusScreen
import com.xfei33.quicktodo.ui.message.MessageScreen
import com.xfei33.quicktodo.ui.profile.ProfileScreen
import com.xfei33.quicktodo.ui.todo.TodoScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController() // 使用独立的 NavController 管理主界面导航

    Scaffold(
        bottomBar = {
            AppBottomBar(navController = navController) // 在主界面显示 AppBottomBar
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "todo",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("todo") { TodoScreen() }
            composable("focus") { FocusScreen() }
            composable("message") { MessageScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}
