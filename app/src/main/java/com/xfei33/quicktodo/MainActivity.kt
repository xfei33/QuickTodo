package com.xfei33.quicktodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xfei33.quicktodo.ui.auth.AuthScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickTodoApp()
        }
    }
}

@Composable
fun QuickTodoApp() {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(navController = navController, startDestination = "auth") {
            composable("auth") { AuthScreen(navController) }
            composable("todo") { TodoScreen() }
            composable("focus") { FocusScreen() }
            composable("message") { MessageScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}

@Composable
fun TodoScreen() {
    // TODO: 待办页面
}

@Composable
fun FocusScreen() {
    // TODO: 专注页面
}

@Composable
fun MessageScreen() {
    // TODO: 消息页面
}

@Composable
fun ProfileScreen() {
    // TODO: 我的页面
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    QuickTodoApp()
}
