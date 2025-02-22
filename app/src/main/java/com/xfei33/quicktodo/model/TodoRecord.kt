package com.xfei33.quicktodo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.UUID

@Entity(tableName = "todo_record")
data class TodoRecord(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val userId: Long,
    val date: LocalDate = LocalDate.now(),
    val completedCount: Int = 0
)
