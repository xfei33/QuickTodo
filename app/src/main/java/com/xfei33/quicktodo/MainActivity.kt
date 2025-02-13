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
import com.xfei33.quicktodo.ui.focus.FocusScreen
import com.xfei33.quicktodo.ui.message.MessageScreen
import com.xfei33.quicktodo.ui.profile.ProfileScreen
import com.xfei33.quicktodo.ui.theme.QuickTodoTheme
import com.xfei33.quicktodo.ui.todo.TodoScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // 启用 Hilt 依赖注入
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickTodoTheme {
                // 设置应用的背景和主题
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuickTodoApp() // 启动应用的 UI
                }
            }
        }
    }
}

@Composable
fun QuickTodoApp() {
    val navController = rememberNavController() // 创建 NavController

    // 设置导航和页面内容
    NavHost(navController = navController, startDestination = "todo") {
        composable("todo") { TodoScreen(navController) }
        composable("focus") { FocusScreen(navController) }
        composable("message") { MessageScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
    }
}
