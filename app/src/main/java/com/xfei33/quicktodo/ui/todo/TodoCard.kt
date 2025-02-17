package com.xfei33.quicktodo.ui.todo

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xfei33.quicktodo.model.Todo
import com.xfei33.quicktodo.ui.theme.QuickTodoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

enum class SwipeDirection {
    None, Left, Right
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTodoCard(
    todo: Todo,
    onEdit: (Todo) -> Unit,
    onComplete: (Todo) -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (Todo) -> Unit
) {
    val swipeThreshold = with(LocalDensity.current) { 150.dp.toPx() } // 滑动阈值
    val coroutineScope = rememberCoroutineScope()
    var swipeOffset by remember { mutableFloatStateOf(0f) }
    var swipeDirection by remember { mutableStateOf(SwipeDirection.None) }
    var isSwiping by remember { mutableStateOf(false) } // 是否正在滑动
    var showDeleteDialog by remember { mutableStateOf(false) } // 是否显示删除对话框
    var showEditDialog by remember { mutableStateOf(false) } // 是否显示编辑对话框

    // 长按动画相关状态
    var longPressPosition by remember { mutableStateOf<Offset?>(null) }
    var circleRadius by remember { mutableStateOf(0f) }
    val animatedRadius by animateFloatAsState(
        targetValue = circleRadius,
        animationSpec = tween(durationMillis = 300)
    )
    // 计算最大半径（足以覆盖整个 Card）
    val maxRadius = with(LocalDensity.current) {
        (1200.dp.toPx()) // 根据 Card 的大小调整
    }

    // 限制滑动最大偏移量
    val maxSwipeOffset = 100f // 设置最大滑动值
    // 限制滑动偏移量范围
    val constrainedSwipeOffset = swipeOffset.coerceIn(-maxSwipeOffset, maxSwipeOffset)

    // 使用动画平滑地回到原始位置
    val animatedOffset by animateFloatAsState(
        targetValue = if (isSwiping) constrainedSwipeOffset else 0f,
        animationSpec = tween(durationMillis = 400),
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isSwiping = true // 开始滑动
                    },
                    onDragEnd = {
                        when {
                            swipeDirection == SwipeDirection.Right -> {
                                coroutineScope.launch {
                                    onComplete(todo) // 右划标记为已完成
                                }
                            }

                            swipeDirection == SwipeDirection.Left -> {
                                coroutineScope.launch {
                                    showEditDialog = true // 左划进入编辑界面
                                }
                            }
                        }
                        isSwiping = false // 结束滑动
                        swipeOffset = 0f // 重置偏移量
                        swipeDirection = SwipeDirection.None // 重置滑动方向
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        swipeOffset += dragAmount
                        when {
                            swipeOffset > swipeThreshold -> {
                                swipeDirection = SwipeDirection.Right
                            }

                            swipeOffset < -swipeThreshold -> {
                                swipeDirection = SwipeDirection.Left
                            }
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        longPressPosition = offset // 记录长按位置
                        circleRadius = 10f // 重置圆形半径
                        coroutineScope.launch {
                            // 启动圆形扩散动画
                            while (circleRadius < maxRadius) {
                                circleRadius += 30f
                                delay(10)
                            }
                            showDeleteDialog = true // 动画完成后显示删除对话框
                            longPressPosition = null // 长按结束，清空记录
                            circleRadius = 0f // 重置圆形半径
                        }
                    }
                )
            }
    ) {
        // 背景指示器
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp, bottom = 8.dp)
                .background(
                    when (swipeDirection) {
                        SwipeDirection.Right -> MaterialTheme.colorScheme.primaryContainer
                        SwipeDirection.Left -> MaterialTheme.colorScheme.secondaryContainer
                        SwipeDirection.None -> Color.Transparent
                    }
                )
                .padding(16.dp),
            contentAlignment = when (swipeDirection) {
                SwipeDirection.Right -> Alignment.CenterStart
                SwipeDirection.Left -> Alignment.CenterEnd
                SwipeDirection.None -> Alignment.Center
            }
        ) {
            Text(
                text = when (swipeDirection) {
                    SwipeDirection.Right -> if (todo.completed) "取消完成" else "已完成"
                    SwipeDirection.Left -> "编辑"
                    SwipeDirection.None -> ""
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // TodoCard
        Box(
            modifier = Modifier
                .offset(x = animatedOffset.dp)
                .fillMaxWidth()
        ) {
            TodoCard(
                todo = todo,
                modifier = Modifier.fillMaxWidth()
            )

            val colorScheme = MaterialTheme.colorScheme
            // 绘制圆形动画
            longPressPosition?.let { position ->
                Canvas(modifier = Modifier
                    .matchParentSize()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)) {
                    clipRect {
                        drawCircle(
                            color = colorScheme.primaryContainer.copy(alpha = 0.5f),
                            radius = animatedRadius,
                            center = position
                        )
                    }
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false // 关闭对话框
            },
            title = {
                Text(text = "删除任务")
            },
            text = {
                Text(text = "确定要删除任务“${todo.title}”吗？")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(todo) // 确认删除
                        showDeleteDialog = false // 关闭对话框
                    }
                ) {
                    Text(text = "删除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false // 关闭对话框
                    }
                ) {
                    Text(text = "取消")
                }
            }
        )
    }

    if (showEditDialog) {
        EditTodoDialog(
            todo = todo,
            onDismiss = { showEditDialog = false },
            onConfirm = { title, description, tag, dueDate, priority ->
                todo.title = title
                todo.description = description
                todo.tag = tag
                todo.dueDate = dueDate
                todo.priority = priority
                onEdit(todo)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun TodoCard(
    todo: Todo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = if (todo.completed) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        } else {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 标题和优先级
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = todo.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        textDecoration = if (todo.completed) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (todo.completed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                    ),
                    fontWeight = FontWeight.Bold
                )
                PriorityIndicator(priority = todo.priority!!)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 描述
            if (todo.description?.isNotBlank() == true) {
                Text(
                    text = todo.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (todo.completed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 截止日期和分类标签
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "截止日期: ${todo.dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (todo.completed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                )
                FilterChip(
                    selected = true,
                    onClick = { /* Handle tag click */ },
                    label = { Text(text = todo.tag) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}


// 优先级指示器
@Composable
fun PriorityIndicator(priority: String) {
    val (text, color) = when (priority) {
        "LOW" -> Pair("低", MaterialTheme.colorScheme.inverseOnSurface)
        "MEDIUM" -> Pair("中", MaterialTheme.colorScheme.secondaryContainer)
        "HIGH" -> Pair("高", MaterialTheme.colorScheme.errorContainer)
        else -> Pair("", MaterialTheme.colorScheme.surface)
    }

    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Composable
fun EditDialog(todo: Todo, onDismiss: () -> Unit, onConfirm: (Todo) -> Unit) {
    EditTodoDialog(
        todo = todo,
        onDismiss = onDismiss,
        onConfirm = { title, description, tag, dueDate, priority -> Unit}
    )
}

@Preview
@Composable
fun PreviewSwipeableTodoCard() {
    QuickTodoTheme {
        val todo = Todo(
            id = UUID.randomUUID(),
            title = "吃饭",
            description = "明天去吃饭",
            dueDate = LocalDateTime.now().plusDays(1),
            userId = 1,
            priority = "MEDIUM",
            completed = true,
            tag = "生活"
        )
        SwipeableTodoCard(
            todo = todo,
            onComplete = { /* Handle complete */ },
            onEdit = { /* Handle edit */ },
            onDelete = { /* Handle delete */ }
        )
    }
}
