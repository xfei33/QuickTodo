package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.UserPreferences
import com.xfei33.quicktodo.data.dao.TodoDao
import com.xfei33.quicktodo.model.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoDao: TodoDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> get() = _todos

    private val _userId = MutableStateFlow<Long>(0L)
    val userId: StateFlow<Long> get() = _userId

    init {
        loadTodos()
    }

    private fun loadTodos() {
        viewModelScope.launch {
            _userId.value = userPreferences.userId.first()!!
            todoDao.getTodosByUser(userId.value).collect { todos ->
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
