// 路径：ui/todos/viewmodel/TodoViewModel.kt
package com.xfei33.quicktodo.ui.todos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.model.TodoItem
import com.xfei33.quicktodo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    private val _username = MutableStateFlow("") // 用户名状态
    val username: StateFlow<String> = _username.asStateFlow()

    // 设置用户名
    fun setUsername(username: String) {
        _username.value = username
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private val _currentDate = mutableStateOf(LocalDate.now())
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate: State<LocalDate> = _currentDate

    // 获取当前日期的待办事项
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTodos(username: String): Flow<List<TodoItem>> {
        val start = _currentDate.value.atStartOfDay()
        val end = _currentDate.value.plusDays(1).atStartOfDay()
        return repository.getTodosByDateRange(username, start, end)
    }

    // 添加待办事项
    fun addTodo(todo: TodoItem) = viewModelScope.launch {
        repository.insertTodo(todo)
    }

    // 切换完成状态
    fun toggleComplete(todo: TodoItem) = viewModelScope.launch {
        repository.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
    }

    // 删除待办事项
    fun deleteTodo(todo: TodoItem) = viewModelScope.launch {
        repository.deleteTodo(todo)
    }
}