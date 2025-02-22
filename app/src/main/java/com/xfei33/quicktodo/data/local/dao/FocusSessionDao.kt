package com.xfei33.quicktodo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xfei33.quicktodo.model.FocusSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface FocusSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: FocusSession)

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId")
    fun getFocusSessionsByUserId(userId: Long): Flow<List<FocusSession>>

    @Update
    suspend fun update(focusSession: FocusSession)

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId AND startTime >= :startTime AND endTime < :endTime")
    fun getFocusSessionsByUserIdAndTimeRange(
        userId: Long,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Flow<List<FocusSession>>
}