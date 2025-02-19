package com.xfei33.quicktodo.ui.todo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xfei33.quicktodo.model.Todo
import com.xfei33.quicktodo.ui.theme.QuickTodoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.math.roundToInt

enum class SwipeDirection {
    None, Left, Right
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTodoCard(
    todo: Todo,
    onEdit: (Todo) -> Unit,
    onComplete: (Todo) -> Unit,
    onDelete: (Todo) -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeThreshold = with(LocalDensity.current) { 100.dp.toPx() }
    val coroutineScope = rememberCoroutineScope()
    val swipeAnimatable = remember { Animatable(0f) }
    val swipeDirection by remember(swipeAnimatable.value) {
        derivedStateOf {
            when {
                swipeAnimatable.value > swipeThreshold -> SwipeDirection.Right
                swipeAnimatable.value < -swipeThreshold -> SwipeDirection.Left
                else -> SwipeDirection.None
            }
        }
    }
    var isSwiping by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var isLongPressing by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { isSwiping = true },
                    onDragEnd = {
                        coroutineScope.launch {
                            when (swipeDirection) {
                                SwipeDirection.Right -> {
                                    onComplete(todo)
                                    swipeAnimatable.animateTo(0f)
                                }
                                SwipeDirection.Left -> {
                                    showEditDialog = true
                                    swipeAnimatable.animateTo(0f)
                                }
                                else -> {
                                    swipeAnimatable.animateTo(0f, tween(durationMillis = 300))
                                }
                            }
                            isSwiping = false
                        }
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        coroutineScope.launch {
                            swipeAnimatable.snapTo(swipeAnimatable.value + dragAmount)
                        }
                    }
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = {
                        showDeleteDialog = true
                        coroutineScope.launch {
                            isLongPressing = true
                            delay(200)
                            isLongPressing = false
                        }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp, bottom = 8.dp)
                .background(
                    when (swipeDirection) {
                        SwipeDirection.Right -> MaterialTheme.colorScheme.primaryContainer
                        SwipeDirection.Left -> MaterialTheme.colorScheme.secondaryContainer
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = when (swipeDirection) {
                SwipeDirection.Right -> Alignment.CenterStart
                SwipeDirection.Left -> Alignment.CenterEnd
                else -> Alignment.Center
            }
        ) {
            Text(
                text = when (swipeDirection) {
                    SwipeDirection.Right -> if (todo.completed) "标记为未完成" else "标记为完成"
                    SwipeDirection.Left -> "编辑"
                    else -> ""
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
        }

        TodoCard(
            todo = todo,
            modifier = Modifier
                .offset { IntOffset(swipeAnimatable.value.roundToInt(), 0) }
                .fillMaxWidth()
                .scale(if (isLongPressing) 0.98f else 1f)
        )

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("删除任务") },
                text = { Text("确定要删除任务“${todo.title}”吗？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDelete(todo)
                            showDeleteDialog = false
                        }
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("取消")
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

            if (todo.description?.isNotBlank() == true) {
                Text(
                    text = todo.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (todo.completed) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

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
