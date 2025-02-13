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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xfei33.quicktodo.components.AppTopBar
import com.xfei33.quicktodo.viewmodel.TodoViewModel

@Composable
fun TodoScreen(navController: NavController) {
    val viewModel: TodoViewModel = hiltViewModel()
    val todos by viewModel.todos.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            AppTopBar(onSearchClick = { /* TODO: Implement search */ })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Open new todo dialog */ }) {
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
                    TodoCard(todo = todo, onEditClick = { /* TODO: Edit todo */ }, onDeleteClick = { viewModel.deleteTodo(it) })
                }
            }
        }
    }
}
