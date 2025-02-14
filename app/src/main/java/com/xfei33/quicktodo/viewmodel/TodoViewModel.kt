package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.UserPreferences
import com.xfei33.quicktodo.data.dao.TodoDao
import com.xfei33.quicktodo.model.Todo
import com.xfei33.quicktodo.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private var lastSyncTime: LocalDateTime = LocalDateTime.MIN

    init {
        loadTodos()
        syncData() // 初始化时同步数据
    }

    private fun loadTodos() {
        viewModelScope.launch {
            _userId.value = userPreferences.userId.first()!!
            todoDao.getNotDeletedTodosByUser(userId.value).collect { todos ->
                _todos.value = todos
            }
        }
    }

    // 增量同步
    private fun syncData() {
        viewModelScope.launch {
            // 从服务端获取增量数据
            val incrementalData = apiService.getIncrementalData(userId.value, lastSyncTime)
            incrementalData.body()?.forEach { todo ->
                if (todo.deleted) {
                    todoDao.delete(todo) // 删除标记为已删除的待办事项
                } else {
                    todoDao.insert(todo) // 添加或更新待办事项
                }
            }

            // 上传客户端的增量数据
            val localIncrementalData = todoDao.getIncrementalData(userId.value, lastSyncTime)
            apiService.uploadIncrementalData(userId.value, localIncrementalData)

            // 更新最后同步时间
            lastSyncTime = LocalDateTime.now()
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
            todo.deleted = true
            todoDao.delete(todo)
        }
    }
}
