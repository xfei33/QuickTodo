package com.xfei33.quicktodo.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyCompletedTasksChart(
    completedTasks: List<Int>,
    modifier: Modifier = Modifier
) {
    val maxTasks = completedTasks.maxOrNull() ?: 0 // 获取最大任务数
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp) // 图表高度
            .padding(16.dp)
    ) {
        val colorScheme = MaterialTheme.colorScheme
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val barWidth = width / (completedTasks.size + 1) // 每个柱子的宽度
            
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

            // 绘制数据柱状图
            completedTasks.forEachIndexed { day, count ->
                val x = day * (barWidth+barWidth/6)
                val barHeight = (count.toFloat() / maxTasks) * height
                
                drawRect(
                    color = colorScheme.primary.copy(alpha = 0.7f),
                    topLeft = Offset(x, height - barHeight),
                    size = Size(barWidth, barHeight)
                )
            }

            // 修改纵坐标标签部分
            if (maxTasks > 0) {
                val step = (maxTasks / 5).coerceIn(1, maxTasks) // 保证步长在1到maxTasks之间
                val yLabels = (0..maxTasks step step).toList()
                
                yLabels.forEach { label ->
                    val yPosition = height - (label.toFloat() / maxTasks) * height
                    drawContext.canvas.nativeCanvas.drawText(
                        label.toString(),
                        -40f,
                        yPosition,
                        android.graphics.Paint().apply {
                            color = colorScheme.onSurface.toArgb()
                            textSize = 30f
                        }
                    )
                }
            } else {
                // 当没有任务时只显示0
                drawContext.canvas.nativeCanvas.drawText(
                    "0",
                    -40f,
                    height,
                    android.graphics.Paint().apply {
                        color = colorScheme.onSurface.toArgb()
                        textSize = 30f
                    }
                )
            }
        }

        // 绘制横坐标标签
        val today = LocalDate.now()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(top = 8.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (day in 0..6) {
                val date = today.minusDays(6 - day.toLong()).format(DateTimeFormatter.ofPattern("MM/dd"))
                Text(
                    text = date,
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
fun PreviewWeeklyCompletedTasksChart() {
    WeeklyCompletedTasksChart(
        completedTasks = listOf(1, 1, 2, 3, 4, 5, 6),
        modifier = Modifier.fillMaxWidth()
    )
} 