package com.xfei33.quicktodo.data.repository

import com.xfei33.quicktodo.data.dao.TodoDao
import com.xfei33.quicktodo.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao
) {
    fun getTodosByDateRange(username: String, start: LocalDateTime, end: LocalDateTime): Flow<List<TodoItem>> {
        return todoDao.getTodosByDate(username, start, end)
    }

    suspend fun insertTodo(todo: TodoItem) {
        todoDao.insert(todo)
    }

    suspend fun updateTodo(todo: TodoItem) {
        todoDao.update(todo)
    }

    suspend fun deleteTodo(todo: TodoItem) {
        todoDao.delete(todo.id)
    }

    fun getAllTodos(username: String): Flow<List<TodoItem>> {
        return todoDao.getAllTodos(username)
    }
}