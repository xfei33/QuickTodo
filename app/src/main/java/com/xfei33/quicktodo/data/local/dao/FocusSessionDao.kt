package com.xfei33.quicktodo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xfei33.quicktodo.model.FocusSession
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Insert
    suspend fun insert(focusSession: FocusSession)

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId ORDER BY startTime DESC")
    fun getFocusSessionsByUserId(userId: Long): Flow<List<FocusSession>>
}