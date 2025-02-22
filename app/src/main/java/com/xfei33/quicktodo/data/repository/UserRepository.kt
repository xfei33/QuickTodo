package com.xfei33.quicktodo.data.repository

import com.xfei33.quicktodo.data.local.dao.UserDao
import com.xfei33.quicktodo.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    fun getUserById(userId: Long): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun addCredits(userId: Long, amount: Int) {
        userDao.addCredits(userId, amount)
    }
} 