package com.xfei33.quicktodo.ui.message

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "消息",
                style = MaterialTheme.typography.headlineLarge
            )
        }
    )
}
@Preview
@Composable
fun PreviewMessageTopBar() {
    MessageTopBar()
}