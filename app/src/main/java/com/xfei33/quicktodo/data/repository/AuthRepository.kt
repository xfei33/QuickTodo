// 路径：data/repository/AuthRepository.kt
package com.xfei33.quicktodo.data.repository

import com.xfei33.quicktodo.data.dao.UserDao
import com.xfei33.quicktodo.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val userDao: UserDao
) {
    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录是否成功
     */
    suspend fun login(username: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val user = userDao.getUserByUsername(username)
            user?.password == password // 验证密码
        }
    }

    /**
     * 注册新用户
     * @param username 用户名
     * @param password 密码
     * @return 注册是否成功（false 表示用户名已存在）
     */
    suspend fun register(username: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                false // 用户名已存在
            } else {
                val user = User(username = username, password = password)
                userDao.insert(user)
                true // 注册成功
            }
        }
    }
}