package com.xfei33.quicktodo.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xfei33.quicktodo.R
import com.xfei33.quicktodo.navigation.NavRoutes

@Composable
fun AppBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        listOf(
            NavigationItem(
                route = NavRoutes.Main.TODO,
                icon = R.drawable.ic_todo,
                label = "待办"
            ),
            NavigationItem(
                route = NavRoutes.Main.FOCUS,
                icon = R.drawable.ic_focus,
                label = "专注"
            ),
            NavigationItem(
                route = NavRoutes.Main.MESSAGE,
                icon = R.drawable.ic_message,
                label = "消息"
            ),
            NavigationItem(
                route = NavRoutes.Main.PROFILE,
                icon = R.drawable.ic_profile,
                label = "我的"
            )
        ).forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // 避免创建重复页面
                            launchSingleTop = true
                            // 恢复到之前的状态
                            restoreState = true
                            // 弹出到起始页面，保留起始页面
                            popUpTo(NavRoutes.Main.TODO) {
                                saveState = true
                            }
                        }
                    }
                }
            )
        }
    }
}

private data class NavigationItem(
    val route: String,
    val icon: Int,
    val label: String
)

@Preview
@Composable
fun AppBottomBarPreview() {
    AppBottomBar(navController = rememberNavController())
}