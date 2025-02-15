package com.xfei33.quicktodo.data.repository

import com.xfei33.quicktodo.data.local.dao.TodoDao
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.data.remote.api.ApiService
import com.xfei33.quicktodo.model.Todo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao,
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {

    // Sync with server
    suspend fun syncWithServer() {
        val lastSyncTime = getLastSyncTime()
        val userId = userPreferences.userId.first()!!
        val token = userPreferences.token.first()!!


        // 上传本地未同步数据
        val localIncrementalData = getIncrementalData(userId, lastSyncTime)
        if (localIncrementalData.isNotEmpty()) {
            val response = apiService.uploadIncrementalData(token, userId, localIncrementalData)
            if (response.isSuccessful) {
                localIncrementalData.forEach { todo ->
                    updateTodo(todo)
                }
            }
        }

        // 拉取服务器最新数据
        val serverIncrementalData = apiService.getIncrementalData(token, userId, lastSyncTime)
        if (serverIncrementalData.isSuccessful) {
            serverIncrementalData.body()?.forEach { serverTodo ->
                val localTodo = getTodoById(serverTodo.id)
                if (localTodo == null) {
                    // 新增
                    insertTodo(serverTodo)
                } else {
                    if (serverTodo.lastModified.isAfter(localTodo.lastModified)) {
                        // 更新
                        updateTodo(serverTodo)
                    }
                }
            }
        }
        updateLastSyncTime(LocalDateTime.now())
    }

    private suspend fun getLastSyncTime(): LocalDateTime {
        val lastSyncTime = userPreferences.lastSyncTime.first()
        return LocalDateTime.parse(lastSyncTime)
    }

    private suspend fun updateLastSyncTime(time: LocalDateTime) {
        userPreferences.saveLastSyncTime(time.toString())
    }

    suspend fun createTodo(title: String, description: String?, dueDate: LocalDateTime, priority: String?, tag: String): Todo {
        val userId = userPreferences.userId.first()!!
        val todo = Todo(
            id = UUID.randomUUID(),
            title = title,
            description = description,
            dueDate = dueDate,
            userId = userId,
            priority = priority,
            tag = tag,
            completed = false,
            deleted = false,
            lastModified = LocalDateTime.now()
        )
        todoDao.insert(todo)
        return todo
    }

    suspend fun insertTodo(todo: Todo) {
        todoDao.insert(todo)
    }

    suspend fun updateTodo(todo: Todo) {
        todo.lastModified = LocalDateTime.now()
        todoDao.update(todo)
    }

    suspend fun deleteTodo(todo: Todo) {
        todoDao.delete(todo)
    }

    fun getTodosByUser(userId: Long): Flow<List<Todo>> {
        return todoDao.getTodosByUser(userId)
    }

    fun getNotDeletedTodosByUser(userId: Long): Flow<List<Todo>> {
        return todoDao.getNotDeletedTodosByUser(userId)
    }

    fun getIncrementalData(userId: Long, lastSyncTime: LocalDateTime): List<Todo> {
        return todoDao.getIncrementalData(userId, lastSyncTime)
    }

    suspend fun getTodoById(id: UUID): Todo? {
        return todoDao.getTodoById(id)
    }
}