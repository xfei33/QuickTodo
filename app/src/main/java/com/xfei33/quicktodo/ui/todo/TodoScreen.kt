package com.xfei33.quicktodo.ui.todo

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xfei33.quicktodo.components.AppTopBar
import com.xfei33.quicktodo.model.Todo
import com.xfei33.quicktodo.viewmodel.TodoViewModel
import java.time.LocalDateTime
import java.util.UUID

@Composable
fun TodoScreen(viewModel: TodoViewModel = hiltViewModel()) {
    val todos by viewModel.todos.collectAsState(initial = emptyList())
    var showDialog by remember { mutableStateOf(false) }

    TodoContent(
        todos = todos,
        onAddTodo = { title, description, tag, dueDate, priority ->
            viewModel.addTodo(title, description, dueDate, priority, tag)
        },
        onDeleteTodo = { todo -> viewModel.deleteTodo(todo) },
        showDialog = showDialog,
        onShowDialogChange = { showDialog = it },
        onCompletedChange = { todo -> viewModel.updateTodoCompletionStatus(todo) },
        onEditTodo = { todo -> viewModel.updateTodo(todo) }
    )
}

@Composable
fun TodoContent(
    todos: List<Todo>,
    onAddTodo: (String, String?, String, LocalDateTime, String) -> Unit,
    onDeleteTodo: (Todo) -> Unit,
    showDialog: Boolean,
    onShowDialogChange: (Boolean) -> Unit,
    onCompletedChange: (Todo) -> Unit,
    onEditTodo: (Todo) -> Unit
) {
    Scaffold(
        topBar = { AppTopBar(onSearchClick = { /* TODO: Search */ }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { onShowDialogChange(true) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Text(
                text = "今日",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(todos) { todo ->
                    SwipeableTodoCard(
                        todo = todo,
                        onEdit = { onEditTodo(todo) },
                        onComplete = { onCompletedChange(todo) },
                        onDelete = { onDeleteTodo(todo) },
                    )
                }
            }
        }
    }

    if (showDialog) {
        NewTodoDialog(
            onDismiss = { onShowDialogChange(false) },
            onConfirm = { title, description, tag, dueDate, priority ->
                onAddTodo(title, description, tag, dueDate, priority)
                onShowDialogChange(false)
            }
        )
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
            onAddTodo = { _, _, _, _, _ -> },
            onDeleteTodo = {},
            showDialog = false,
            onShowDialogChange = {},
            onCompletedChange = {},
            onEditTodo = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTodoContentWithDialog() {
    MaterialTheme {
        TodoContent(
            todos = emptyList(),
            onAddTodo = { _, _, _, _, _ -> },
            onDeleteTodo = {},
            showDialog = true,
            onShowDialogChange = {},
            onCompletedChange = {},
            onEditTodo = {}
        )
    }
}


