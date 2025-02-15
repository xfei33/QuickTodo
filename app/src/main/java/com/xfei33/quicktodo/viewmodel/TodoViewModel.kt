package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.UserPreferences
import com.xfei33.quicktodo.data.dao.TodoDao
import com.xfei33.quicktodo.model.Todo
import com.xfei33.quicktodo.network.ApiService
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
    private val apiService: ApiService
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> get() = _todos

    private val _userId = MutableStateFlow<Long>(0L)
    val userId: StateFlow<Long> get() = _userId

    private val _token = MutableStateFlow<String?>("")
    val token: StateFlow<String?> get() = _token

    init {
        viewModelScope.launch() {
            _userId.value = userPreferences.userId.first()!!
            _token.value = userPreferences.token.first()
            syncData()
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

    // 增量同步
    private fun syncData() {
        viewModelScope.launch(Dispatchers.IO) {
            val localLatestTime = todoDao.getLastSyncTime(userId.value) ?: LocalDateTime.parse("0001-01-01T00:00:00")
            val serverLatestTime = apiService.getLatestTime(token.value!!, userId.value).body() ?: LocalDateTime.parse("0001-01-01T00:00:00")

            // 服务器数据比本地新
            if (localLatestTime < serverLatestTime) {
                val incrementalData = apiService.getIncrementalData(token.value!!, userId.value, localLatestTime)
                incrementalData.body()?.forEach { todo ->
                    todoDao.insert(todo) // 添加或更新待办事项
                }
            } else if (localLatestTime > serverLatestTime) {
                // 本地数据比服务器新，需要上传本地数据
                val localIncrementalData = todoDao.getIncrementalData(userId.value, serverLatestTime)
                apiService.uploadIncrementalData(token.value!!, userId.value, localIncrementalData)
            } else {
                // 本地数据和服务器数据相同，不需要同步
            }
        }
    }


    fun addTodo(todo: Todo) {
        viewModelScope.launch {
            todo.lastModified = LocalDateTime.now()
            todoDao.insert(todo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todo.lastModified = LocalDateTime.now()
            todoDao.update(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todo.lastModified = LocalDateTime.now()
            todo.deleted = true
        }
    }
}
