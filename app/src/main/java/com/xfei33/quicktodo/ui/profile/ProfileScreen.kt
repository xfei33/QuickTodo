package com.xfei33.quicktodo.ui.profile

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.xfei33.quicktodo.R
import com.xfei33.quicktodo.viewmodel.ProfileViewModel

@Composable
fun TimeDistributionChart(
    focusData: List<Int>,
    modifier: Modifier = Modifier
) {
    val maxMinutes = 60 // 将最大值固定为60
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp) // 增加图表高度
            .padding(16.dp)
    ) {
        val colorScheme = MaterialTheme.colorScheme
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height // 使用动态高度
            val barWidth = width / (focusData.size + 1) // 增加间距
            
            // 绘制坐标轴
            drawLine(
                color = colorScheme.onSurface,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 2f
            )
            
            drawLine(
                color = colorScheme.onSurface,
                start = Offset(0f, 0f),
                end = Offset(0f, height),
                strokeWidth = 2f
            )

            // 绘制分割线
            val verticalLineColor = colorScheme.onSurface.copy(alpha = 0.2f) // 垂直分割线颜色
            val horizontalLineColor = colorScheme.onSurface.copy(alpha = 0.2f) // 水平分割线颜色
            
//            // 绘制纵坐标分割线
//            val yLabels = listOf(15, 30, 45, 60)
//            yLabels.forEach { label ->
//                val yPosition = height - (label.toFloat() / maxMinutes) * height
//                drawLine(
//                    color = verticalLineColor,
//                    start = Offset(0f, yPosition),
//                    end = Offset(width, yPosition),
//                    strokeWidth = 1f
//                )
//            }
//
//            // 绘制横坐标分割线
//            for (hour in 0 until focusData.size) {
//                val xPosition = (hour * barWidth) + (barWidth / 2) // 调整位置
//                drawLine(
//                    color = horizontalLineColor,
//                    start = Offset(xPosition, 0f),
//                    end = Offset(xPosition, height),
//                    strokeWidth = 1f
//                )
//            }

            // 绘制数据柱状图
            focusData.forEachIndexed { hour, minutes ->
                val x = hour * (barWidth + barWidth/23) // 调整柱状图位置
                val barHeight = (minutes.toFloat() / maxMinutes) * height
                
                drawRect(
                    color = colorScheme.primary.copy(alpha = 0.7f),
                    topLeft = Offset(x, height - barHeight),
                    size = Size(barWidth, barHeight) // 增加柱状图间距
                )
            }

            // 绘制Y轴标签
            val labelPositions = listOf(15, 30, 45, 60)
            labelPositions.forEach { label ->
                val yPosition = height - (label.toFloat() / maxMinutes) * height
                drawContext.canvas.nativeCanvas.drawText(
                    label.toString(),
                    -40f, // 将标签放到坐标轴外侧
                    yPosition,
                    Paint().apply {
                        color = colorScheme.onSurface.toArgb()
                        textSize = 30f // 设置字体大小
                    }
                )
            }
        }
        // 绘制X轴标签
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(top = 8.dp, ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (hour in 0..24 step 6) {
                Text(
                    text = String.format("%02d:00", hour),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .offset(y = 15.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTimeDistributionChart() {
    TimeDistributionChart(
        focusData = (0..23).map { it * 1 },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val focusData by viewModel.focusData.collectAsState()
    val weeklyCompletedTasks by viewModel.weeklyCompletedTasks.collectAsState()

    user?.let { currentUser ->
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "我的",
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically // 垂直居中对齐
                    ) {
                        // 用户头像带边框
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .padding(2.dp)
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(currentUser.headshot)
                                        .diskCachePolicy(CachePolicy.ENABLED)
                                        .memoryCachePolicy(CachePolicy.ENABLED)
                                        .build(),
                                    error = painterResource(id = R.drawable.ic_avatar)
                                ),
                                contentDescription = "用户头像",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp)) // 添加间距

                        // 其他信息
                        Column(
                            modifier = Modifier.weight(1f), // 让其他信息占据剩余空间
                            horizontalAlignment = Alignment.Start // 左对齐
                        ) {
                            // 用户昵称
                            Text(
                                text = currentUser.nickname,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp)) // 添加间距

                            // 碳排放减少量
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_leaves),
                                    contentDescription = "碳排放图标",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = "减少碳排放量：${currentUser.credits}g",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 添加专注时间分布卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "今日专注时间分布",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        TimeDistributionChart(
                            focusData = focusData,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 添加每周完成任务数量的图表
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "最近7天完成任务数量",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        WeeklyCompletedTasksChart(
                            completedTasks = weeklyCompletedTasks,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}