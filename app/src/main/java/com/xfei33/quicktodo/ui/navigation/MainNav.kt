package com.xfei33.quicktodo.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xfei33.quicktodo.ui.auth.LoginScreen
import com.xfei33.quicktodo.ui.auth.RegisterScreen
import com.xfei33.quicktodo.ui.components.QuickTodoBottomAppBar
import com.xfei33.quicktodo.ui.focus.FocusScreen
import com.xfei33.quicktodo.ui.profile.ProfileScreen
import com.xfei33.quicktodo.ui.todos.TodoListScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainNav() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 根据当前路由自动更新选中状态
    val selectedTab = remember { mutableStateOf(getTabIndex(currentRoute)) }

    Scaffold(
        bottomBar = {
            QuickTodoBottomAppBar(
                selectedTab = selectedTab.value,
                onTabSelected = { tab ->
                    selectedTab.value = tab
                    when (tab) {
                        0 -> navController.navigate("todos")
                        1 -> navController.navigate("focus")
                        2 -> navController.navigate("profile")
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { username ->
                        // 统一导航到不带参数的todos路由
                        navController.navigate("todos") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = { username ->
                        navController.navigate("todos") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                )
            }

            // 修改为不带参数的todos路由
            composable("todos") {
                // 通过ViewModel传递用户名（需调整ViewModel逻辑）
                TodoListScreen()
            }

            composable("focus") {
                FocusScreen()
            }

            composable("profile") {
                ProfileScreen()
            }
        }
    }
}

// 根据路由解析当前选中Tab
private fun getTabIndex(route: String?): Int {
    return when {
        route?.startsWith("todos") == true -> 0
        route == "focus" -> 1
        route == "profile" -> 2
        else -> -1 // 非底部导航页面返回-1
    }
}