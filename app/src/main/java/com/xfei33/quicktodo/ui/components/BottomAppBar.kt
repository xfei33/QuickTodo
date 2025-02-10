package com.xfei33.quicktodo.ui.components

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.xfei33.quicktodo.R

@Composable
fun QuickTodoBottomAppBar(
    selectedTab: Int, // 当前选中的 Tab
    onTabSelected: (Int) -> Unit // Tab 点击回调
) {
    BottomAppBar {
        // 待办按钮
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_todo), // 待办图标
                    contentDescription = "待办"
                )
            },
            label = { Text("待办") }
        )

        // 专注按钮
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_focus), // 专注图标
                    contentDescription = "专注"
                )
            },
            label = { Text("专注") }
        )

        // 账户按钮
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_account), // 账户图标
                    contentDescription = "账户"
                )
            },
            label = { Text("账户") }
        )
    }
}