// 路径：ui/focus/FocusScreen.kt
package com.xfei33.quicktodo.ui.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// 定义专注模式枚举
enum class TimerMode {
    POMODORO, COUNTDOWN
}

@Composable
fun FocusScreen() {
    var timerMode by remember { mutableStateOf(TimerMode.POMODORO) }
    var remainingTime by remember { mutableStateOf(25 * 60) } // 默认 25 分钟
    var isRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (remainingTime > 0) {
                delay(1000)
                remainingTime--
            }
            // 计时结束，播放音效或通知
            isRunning = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 显示剩余时间
        Text(
            text = "%02d:%02d".format(remainingTime / 60, remainingTime % 60),
            style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 开始/暂停按钮
        Button(onClick = { isRunning = !isRunning }) {
            Text(if (isRunning) "暂停" else "开始")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 模式选择器
        TimerModeSelector(
            currentMode = timerMode,
            onModeSelected = { timerMode = it }
        )
    }
}

@Composable
fun TimerModeSelector(
    currentMode: TimerMode,
    onModeSelected: (TimerMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimerMode.entries.forEach { mode ->
            Button(
                onClick = { onModeSelected(mode) },
                enabled = currentMode != mode
            ) {
                Text(text = when (mode) {
                    TimerMode.POMODORO -> "番茄钟"
                    TimerMode.COUNTDOWN -> "倒计时"
                })
            }
        }
    }
}