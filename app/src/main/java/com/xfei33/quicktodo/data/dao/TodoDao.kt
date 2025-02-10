package com.xfei33.quicktodo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xfei33.quicktodo.data.model.TodoItem
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_items WHERE createdBy = :username AND dueDate BETWEEN :start AND :end")
    fun getTodosByDate(username: String, start: LocalDateTime, end: LocalDateTime): Flow<List<TodoItem>>

    @Insert
    suspend fun insert(todo: TodoItem)

    @Update
    suspend fun update(todo: TodoItem)

    @Query("DELETE FROM todo_items WHERE id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * FROM todo_items WHERE createdBy = :username")
    fun getAllTodos(username: String): Flow<List<TodoItem>>
}