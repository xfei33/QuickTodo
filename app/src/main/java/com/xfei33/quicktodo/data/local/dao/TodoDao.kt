package com.xfei33.quicktodo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.xfei33.quicktodo.model.Todo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.UUID

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos WHERE userId = :userId ORDER BY dueDate ASC")
    fun getTodosByUser(userId: Long): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE userId = :userId  AND deleted = 0 ORDER BY dueDate ASC")
    fun getNotDeletedTodosByUser(userId: Long): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE userId = :userId AND lastModified > :lastSyncTime")
    fun getIncrementalData(userId: Long, lastSyncTime: LocalDateTime): List<Todo>

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: UUID): Todo?

    @Insert
    suspend fun insert(todo: Todo)

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)
}
