package com.xfei33.quicktodo.ui.todo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xfei33.quicktodo.model.Todo
import com.xfei33.quicktodo.viewmodel.TodoViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Composable
fun TodoScreen(viewModel: TodoViewModel = hiltViewModel()) {
    val todos by viewModel.todos.collectAsState(initial = emptyList())
    val showTodayOnly by viewModel.showTodayOnly.collectAsState(initial = true)

    TodoContent(
        todos = todos,
        showTodayOnly = showTodayOnly,
        onToggleView = { viewModel.toggleShowTodayOnly() },
        onAddTodo = { title, description, tag, dueDate, priority ->
            viewModel.addTodo(title, description, tag, dueDate, priority)
        },
        onDeleteTodo = { todo -> viewModel.deleteTodo(todo) },
        onCompletedChange = { todo -> viewModel.updateTodoCompletionStatus(todo) },
        onEditTodo = { todo -> viewModel.updateTodo(todo) },
        onSearch = { query -> viewModel.searchTodos(query) }
    )
}

@Composable
fun TodoContent(
    todos: List<Todo>,
    showTodayOnly: Boolean,
    onToggleView: () -> Unit,
    onAddTodo: (String, String?, String, LocalDateTime, String) -> Unit,
    onDeleteTodo: (Todo) -> Unit,
    onCompletedChange: (Todo) -> Unit,
    onEditTodo: (Todo) -> Unit,
    onSearch: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                showTodayOnly = showTodayOnly,
                onToggleView = onToggleView,
                onSearchTextChange = { query -> onSearch(query) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加待办")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(
                text = "花开堪折直须折，莫待无花空折枝。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 20.dp, bottom = 16.dp)
            )

            if (todos.isEmpty()) {
                EmptyState()
            } else if (showTodayOnly) {
                // 今日视图
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(todos) { todo ->
                        SwipeableTodoCard(
                            todo = todo,
                            onEdit = { onEditTodo(todo) },
                            onComplete = { onCompletedChange(todo) },
                            onDelete = { onDeleteTodo(todo) }
                        )
                    }
                }
            } else {
                // 日程视图
                val groupedTodos = todos.groupBy { it.dueDate.toLocalDate() }
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    groupedTodos.forEach { (date, todosForDate) ->
                        item {
                            Text(
                                text = formatDate(date),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        items(todosForDate) { todo ->
                            SwipeableTodoCard(
                                todo = todo,
                                onEdit = { onEditTodo(todo) },
                                onComplete = { onCompletedChange(todo) },
                                onDelete = { onDeleteTodo(todo) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        NewTodoDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { title, description, tag, dueDate, priority ->
                onAddTodo(title, description, tag, dueDate, priority)
                showCreateDialog = false
            }
        )
    }
}

private fun formatDate(date: LocalDate): String {
    val yesterday = LocalDate.now().minusDays(1)
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)
    
    return when(date) {
        yesterday -> "昨天"
        today -> "今天"
        tomorrow -> "明天"
        else -> date.format(DateTimeFormatter.ofPattern("M月d日"))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoContent() {
    MaterialTheme {
        TodoContent(
            todos = listOf(
                Todo(
                    id = UUID.randomUUID(),
                    title = "完成项目报告",
                    description = "撰写项目总结",
                    dueDate = LocalDateTime.now(),
                    userId = 1,
                    priority = "HIGH",
                    completed = false,
                    tag = "Work",
                    lastModified = LocalDateTime.now(),
                    deleted = false
                ),
                Todo(
                    id = UUID.randomUUID(),
                    title = "购买食材",
                    description = "牛奶、面包、鸡蛋",
                    dueDate = LocalDateTime.now(),
                    userId = 1,
                    priority = "LOW",
                    completed = true,
                    tag = "Shopping",
                    lastModified = LocalDateTime.now(),
                    deleted = false
                )
            ),
            showTodayOnly = true,
            onToggleView = {},
            onAddTodo = { _, _, _, _, _ -> },
            onDeleteTodo = {},
            onCompletedChange = {},
            onEditTodo = {},
            onSearch = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoContentWithDialog() {
    MaterialTheme {
        TodoContent(
            todos = emptyList(),
            showTodayOnly = true,
            onToggleView = {},
            onAddTodo = { _, _, _, _, _ -> },
            onDeleteTodo = {},
            onCompletedChange = {},
            onEditTodo = {},
            onSearch = {}
        )
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "这儿没什么事",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        Text(
            text = "去休息休息吧☺\uFE0F",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}


