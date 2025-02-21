package com.xfei33.quicktodo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: Long = 0,
    val nickname: String = "离线用户",
    val headshot: String = "android.resource://com.xfei33.quicktodo/drawable/ic_avatar", // 默认头像资源路径
    val credits: Int = 0 // 碳积分
)
