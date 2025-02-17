package com.xfei33.quicktodo.ui.focus

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.xfei33.quicktodo.service.TimerService
import com.xfei33.quicktodo.viewmodel.FocusViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Preview(showBackground = true)
@Composable
fun FocusScreenPreview() {
    val previewNavController = rememberNavController()
    MaterialTheme {
        Surface {
            FocusScreen(navController = previewNavController)
        }
    }
}

@Composable
fun FocusScreen(
    navController: NavController,
    viewModel: FocusViewModel = hiltViewModel()
) {
    val timerState by viewModel.timerState.collectAsState()
    val remainingSeconds by viewModel.remainingSeconds.collectAsState()
    val selectedMinutes by viewModel.selectedMinutes.collectAsState()
    val isCountDown by viewModel.isCountDown.collectAsState()
    val pausedSeconds by viewModel.pausedSeconds.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var timerService by remember { mutableStateOf<TimerService?>(null) }

    // 修改服务绑定逻辑
    DisposableEffect(Unit) {
        val serviceIntent = Intent(context, TimerService::class.java)
        // 启动服务以确保其在后台运行
        context.startService(serviceIntent)

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as TimerService.TimerBinder
                timerService = binder.getService()
                // 连接后立即同步状态
                scope.launch {
                    timerService?.let { service ->
                        viewModel.updateTimerState(service.timerState.value)
                        viewModel.updateRemainingSeconds(service.remainingSeconds.value)
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                timerService = null
            }
        }

        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

        onDispose {
            // 解绑服务但不停止它
            context.unbindService(serviceConnection)
        }
    }

    // 监听服务状态
    LaunchedEffect(timerService) {
        timerService?.let { service ->
            scope.launch {
                service.remainingSeconds.collect { seconds ->
                    viewModel.updateRemainingSeconds(seconds)
                }
            }
            scope.launch {
                service.timerState.collect { state ->
                    viewModel.updateTimerState(state)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            FocusScreenTopBar(
                isCountDown = isCountDown,
                onModeChange = { viewModel.updateIsCountDown(it) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                // 中间部分：时间显示和进度
                Box(
                    modifier = Modifier
                        .height(280.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val progress = if (isCountDown && timerState != TimerState.IDLE) {
                        remainingSeconds.toFloat() / (selectedMinutes * 60)
                    } else 1f

                    // 在 Canvas 外部获取颜色
                    val primaryColor = MaterialTheme.colorScheme.primary

                    Canvas(
                        modifier = Modifier
                            .size(280.dp)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onDoubleTap = {
                                        when (timerState) {
                                            TimerState.RUNNING -> {
                                                viewModel.updatePausedSeconds(remainingSeconds)
                                                timerService?.pauseTimer()
                                            }
                                            TimerState.PAUSED -> {
                                                timerService?.resumeTimer(pausedSeconds, isCountDown)
                                            }
                                            else -> {}
                                        }
                                    }
                                )
                            }
                    ) {
                        val strokeWidth = 15.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

                        drawArc(
                            color = primaryColor.copy(alpha = 0.3f),
                            startAngle = 0f,
                            sweepAngle = 360f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(diameter, diameter),
                            style = Stroke(width = strokeWidth)
                        )

                        drawArc(
                            color = primaryColor,
                            startAngle = -90f,
                            sweepAngle = 360f * progress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(diameter, diameter),
                            style = Stroke(width = strokeWidth)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 时间显示
                        Text(
                            text = formatTime(remainingSeconds),
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }


                Text(
                    text = "计时开始时，通知将被暂时屏蔽",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 设置时间滑块（仅在空闲状态显示）
                if (timerState == TimerState.IDLE) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${selectedMinutes.toInt()} 分钟",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Slider(
                                value = selectedMinutes,
                                onValueChange = { viewModel.updateSelectedMinutes(it) },
                                valueRange = 5f..180f,
                                steps = 174,  // (180-5)
                                modifier = Modifier.fillMaxWidth(),
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary,
                                    inactiveTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f)
                                )
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                //.padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "5分钟",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = "120分钟",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceEvenly
//                        ) {
//                            QuickTimeButton(45, selectedMinutes, onTimeSelected = { selectedMinutes = it.toFloat() })
//                            QuickTimeButton(60, selectedMinutes, onTimeSelected = { selectedMinutes = it.toFloat() })
//                            QuickTimeButton(120, selectedMinutes, onTimeSelected = { selectedMinutes = it.toFloat() })
//                        }
                    }
                }

                // 底部按钮
                Button(
                    onClick = {
                        when (timerState) {
                            TimerState.IDLE -> {
                                val totalSeconds = selectedMinutes.toInt() * 60L
                                timerService?.startTimer(totalSeconds, isCountDown)
                            }
                            TimerState.RUNNING, TimerState.PAUSED -> {
                                timerService?.stopTimer()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = when (timerState) {
                            TimerState.IDLE -> if (isCountDown) "开始专注" else "开始计时"
                            TimerState.RUNNING -> "结束"
                            TimerState.PAUSED -> "结束"
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

enum class TimerState {
    IDLE, RUNNING, PAUSED
}

fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Composable
private fun QuickTimeButton(
    minutes: Int,
    selectedMinutes: Float,
    onTimeSelected: (Int) -> Unit
) {
    OutlinedButton(
        onClick = { onTimeSelected(minutes) },
        modifier = Modifier.padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selectedMinutes.toInt() == minutes)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (selectedMinutes.toInt() == minutes)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = "${minutes}分钟",
            style = MaterialTheme.typography.labelMedium,
            color = if (selectedMinutes.toInt() == minutes)
                MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

fun startTimer(scope: CoroutineScope, isCountDown: Boolean) {
    var remainingSeconds = if (isCountDown) (25 * 60L) else 0L
    val job = scope.launch {
        while (true) {
            delay(1000)
            if (isCountDown) {
                remainingSeconds--
                if (remainingSeconds <= 0) {
                    break
                }
            } else {
                remainingSeconds++
            }
        }
    }
}