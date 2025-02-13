package com.xfei33.quicktodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xfei33.quicktodo.ui.auth.AuthScreen
import com.xfei33.quicktodo.ui.main.MainScreen
import com.xfei33.quicktodo.ui.theme.QuickTodoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickTodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuickTodoApp()
                }
            }
        }
    }
}

@Composable
fun QuickTodoApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "auth" // 启动后首先显示身份验证界面
    ) {
        composable("auth") { AuthScreen(navController) } // 身份验证界面
        composable("main") { MainScreen() } // 主界面
    }
}
