package com.xfei33.quicktodo.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(onSearchClick: () -> Unit) {
    TopAppBar(
        title = { Text("QuickTodo") },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        }
    )
}

@Preview
@Composable
fun AppTopBarPreview() {
    AppTopBar(onSearchClick = { /*TODO*/ })
}
