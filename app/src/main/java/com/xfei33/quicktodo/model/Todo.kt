package com.xfei33.quicktodo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String,
    var description: String? = null,
    var dueDate: LocalDateTime,
    val userId: Long,
    var priority: String?,
    var completed: Boolean = false,
    var tag: String,
    var lastModified: LocalDateTime = LocalDateTime.now(),
    var deleted: Boolean = false
)
