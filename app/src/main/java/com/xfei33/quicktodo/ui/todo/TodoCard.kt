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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp), // Added padding around the whole card
            verticalAlignment = Alignment.Top
        ) {
            Checkbox(
                checked = todo.completed,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.width(12.dp)) // Added spacing between checkbox and content

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp) // Adjusted spacing between title and priority
                ) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    PriorityChip(priority = todo.priority!!)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Display description if available
                if (todo.description?.isNotBlank() == true) {
                    Text(
                        text = todo.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = todo.dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = false,
                        onClick = { /* Handle tag click */ },
                        label = { Text(text = todo.tag) },
                        modifier = Modifier.padding(start = 8.dp), // Added padding for better spacing
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PriorityChip(priority: String) {
    val (text, color) = when (priority) {
        "LOW" -> Pair("低", MaterialTheme.colorScheme.secondaryContainer)
        "MEDIUM" -> Pair("中", MaterialTheme.colorScheme.primaryContainer)
        "HIGH" -> Pair("高", MaterialTheme.colorScheme.errorContainer)
        else -> Pair("", MaterialTheme.colorScheme.surface)
    }

    Box(
        modifier = Modifier
            .background(color, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp) // Added padding to increase button size
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
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
            completed = false,
            tag = "家常菜"
        )
        TodoCard(todo = todo, onCheckedChange = { /* Handle checked change */ })
    }
}
