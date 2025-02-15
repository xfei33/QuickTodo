package com.xfei33.quicktodo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val title: String,
    val description: String? = null,
    val dueDate: LocalDateTime,
    val userId: Long,
    val priority: String?,
    val completed: Boolean = false,
    val tag: String,
    var lastModified: LocalDateTime = LocalDateTime.now(),
    var deleted: Boolean = false
)
