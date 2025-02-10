package com.xfei33.quicktodo.ui.todos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.hilt.navigation.compose.hiltViewModel
import com.xfei33.quicktodo.data.model.TodoItem

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodoListScreen(viewModel: TodoViewModel = hiltViewModel()) {
    val username by viewModel.username.collectAsState()
    Text(text = "欢迎回来，$username")

    val todos by viewModel.getTodos(username).collectAsState(initial = emptyList())

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(todos) { todo ->
            TodoItemCard(
                todo = todo,
                onCheckedChange = { viewModel.toggleComplete(todo) },
                onDelete = { viewModel.deleteTodo(todo) }
            )
        }
    }
}

@Composable
fun TodoItemCard(todo: TodoItem, onCheckedChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = todo.isCompleted,
                onCheckedChange = onCheckedChange
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    style = if (todo.isCompleted) {
                        LocalTextStyle.current.copy(
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        LocalTextStyle.current
                    }
                )
                Text(text = "优先级：${todo.priority}")
                Text(text = todo.category)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "删除")
            }
        }
    }
}