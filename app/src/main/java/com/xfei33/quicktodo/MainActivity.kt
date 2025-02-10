package com.xfei33.quicktodo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.xfei33.quicktodo.ui.navigation.MainNav
import com.xfei33.quicktodo.ui.theme.QuickTodoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuickTodoTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainNav() // 启动导航逻辑
                }
            }
        }
    }
}