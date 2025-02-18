package com.xfei33.quicktodo.model

import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.UUID

@Entity(tableName = "messages")
data class Message(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    val icon: ImageBitmap,  // 图标
    val title: String, // 标题
    val sender: String, // 发送者
    val content: String, // 内容
    val category: Int, // 分类
    val isRead: Boolean, // 是否已读
    val time: LocalDateTime // 时间
)
