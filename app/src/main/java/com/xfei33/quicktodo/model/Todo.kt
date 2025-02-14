package com.xfei33.quicktodo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dueDate: LocalDateTime,
    val userId: Long,
    val priority: String?,
    val completed: Boolean = false,
    val tag: String,
    val lastModified: LocalDateTime = LocalDateTime.now(),
    var deleted: Boolean = false
)
