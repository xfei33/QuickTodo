package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.xfei33.quicktodo.data.local.dao.TodoDao
import com.xfei33.quicktodo.data.local.dao.UserDao
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.data.repository.TodoRepository
import com.xfei33.quicktodo.model.Todo
import com.xfei33.quicktodo.worker.NotificationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoDao: TodoDao,
    private val userDao: UserDao,
    private val userPreferences: UserPreferences,
    private val todoRepository: TodoRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> get() = _todos

    private val _userId = MutableStateFlow<Long>(0L)
    val userId: StateFlow<Long> get() = _userId

    private val _showTodayOnly = MutableStateFlow(true)
    val showTodayOnly: StateFlow<Boolean> = _showTodayOnly

    private var originalTodos = emptyList<Todo>()

    init {
        viewModelScope.launch {
            _userId.value = userPreferences.userId.first()!!
            loadTodos()
        }
    }

    private fun loadTodos() {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.getNotDeletedTodosByUser(userId.value).collect { todos ->
                originalTodos = todos
                filterTodos()
            }
        }
    }

    private fun filterTodos() {
        val filteredTodos = if (showTodayOnly.value) {
            val today = LocalDate.now()
            originalTodos.filter { todo ->
                todo.dueDate.toLocalDate() == today
            }
        } else {
            originalTodos
        }
        _todos.value = filteredTodos
    }

    fun toggleShowTodayOnly() {
        _showTodayOnly.value = !_showTodayOnly.value
        filterTodos()
    }

    fun searchTodos(query: String) {
        if (query.isBlank()) {
            filterTodos()
            return
        }

        val searchResults = originalTodos.filter { todo ->
            todo.title.contains(query, ignoreCase = true) ||
                    (todo.description?.contains(query, ignoreCase = true) ?: false) ||
                    todo.tag.contains(query, ignoreCase = true)
        }

        _todos.value = if (showTodayOnly.value) {
            val today = LocalDate.now()
            searchResults.filter { todo ->
                todo.dueDate.toLocalDate() == today
            }
        } else {
            searchResults
        }
    }

    // 切换todo的完成状态
    fun updateTodoCompletionStatus(todo: Todo) {
        viewModelScope.launch {
            todo.completed = !todo.completed
            todoRepository.updateTodo(todo)
            if (todo.completed) {
                userDao.addCredits(userId.value, 10)
            }
        }
    }

    fun addTodo(title: String, description: String?, tag: String, dueDate: LocalDateTime, priority: String?) {
        viewModelScope.launch {
            val newTodo = todoRepository.createTodo(
                title = title,
                description = description,
                dueDate = dueDate,
                priority = priority,
                tag = tag
            )
            // 调度通知
            scheduleNotification(newTodo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo)
            // 重新调度通知
            scheduleNotification(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todo)
            // 取消已调度的通知
            cancelNotification(todo)
        }
    }

    private fun scheduleNotification(todo: Todo) {
        val notificationTime = todo.dueDate.minusHours(1)
        val currentTime = LocalDateTime.now()
        val delayMillis = Duration.between(currentTime, notificationTime).toMillis()

        if (delayMillis > 0) {
            val data = Data.Builder()
                .putString(NotificationWorker.TODO_ID, todo.id.toString())
                .putString(NotificationWorker.TODO_TITLE, todo.title)
                .putString(NotificationWorker.TODO_CONTENT, todo.description ?: "您有一个待办事项即将到期。")
                .build()

            val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInitialDelay(delayMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("todo_notification_${todo.id}")
                .build()

            workManager.enqueueUniqueWork(
                "todo_notification_${todo.id}",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }

    private fun cancelNotification(todo: Todo) {
        workManager.cancelUniqueWork("todo_notification_${todo.id}")
    }
}

