// 路径：data/dao/UserDao.kt
package com.xfei33.quicktodo.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xfei33.quicktodo.data.model.User

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}