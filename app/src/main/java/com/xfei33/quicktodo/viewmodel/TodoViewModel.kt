package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.dao.TodoDao
import com.xfei33.quicktodo.model.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoDao: TodoDao
) : ViewModel() {
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> get() = _todos

    init {
        loadTodos()
    }

    private fun loadTodos() {
        viewModelScope.launch {
            todoDao.getTodosByUser(1).collect { todos ->
                _todos.value = todos
            }
        }
    }

    fun addTodo(todo: Todo) {
        viewModelScope.launch {
            todoDao.insert(todo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoDao.update(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoDao.delete(todo)
        }
    }
}
