package com.xfei33.quicktodo.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String? = null,
    val category: String,
    val priority: Int,
    val dueDate: LocalDateTime,
    val isCompleted: Boolean = false,
    val createdBy: String // 用户名
)
