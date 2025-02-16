package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.local.dao.TodoDao
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.data.repository.TodoRepository
import com.xfei33.quicktodo.model.Todo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoDao: TodoDao,
    private val userPreferences: UserPreferences,
    private val todoRepository: TodoRepository,
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> get() = _todos

    private val _userId = MutableStateFlow<Long>(0L)
    val userId: StateFlow<Long> get() = _userId

    init {
        viewModelScope.launch() {
            _userId.value = userPreferences.userId.first()!!
            loadTodos()
        }
    }

    private fun loadTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.getNotDeletedTodosByUser(userId.value).collect { todos ->
                _todos.value = todos
            }
        }
    }

    // 切换todo的完成状态
    fun updateTodoCompletionStatus(todo: Todo) {
        todo.completed =!todo.completed
        updateTodo(todo)
    }

    fun addTodo(title: String, description: String?, dueDate: LocalDateTime, priority: String?, tag: String) {
        viewModelScope.launch {
            todoRepository.createTodo(
                title = title,
                description = description,
                dueDate = dueDate,
                priority = priority,
                tag = tag
            )
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo)
        }
    }
}
