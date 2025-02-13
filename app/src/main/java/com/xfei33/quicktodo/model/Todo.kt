package com.xfei33.quicktodo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val dueDate: Date,
    val userId: Long,
    val priority: String?,
    val completed: Boolean = false,
    val tag: String,
    val lastModified: Date = Date()
)
