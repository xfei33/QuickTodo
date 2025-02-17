package com.xfei33.quicktodo.ui.focus

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusScreenTopBar(
    isCountDown: Boolean,
    onModeChange: (Boolean) -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "专注",
                style = MaterialTheme.typography.headlineLarge
            )
        },
        actions = {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = if (isCountDown) "倒计时" else "正计时",
                    style = MaterialTheme.typography.bodyMedium
                )
                Switch(
                    checked = isCountDown,
                    onCheckedChange = onModeChange,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    )
}

@Preview
@Composable
fun PreviewFocusScreenTopBar() {
    FocusScreenTopBar(true) { _ -> }
}