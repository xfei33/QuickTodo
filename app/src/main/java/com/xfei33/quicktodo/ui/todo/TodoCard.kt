package com.xfei33.quicktodo.ui.todo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xfei33.quicktodo.model.Todo
import com.xfei33.quicktodo.ui.theme.QuickTodoTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun TodoCard(
    todo: Todo,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
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
                    text = todo.description,
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
fun PreviewTodoCard() {
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
        TodoCard(todo = todo, onCheckedChange = { /* Handle checked change */ })
    }
}

