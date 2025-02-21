package com.xfei33.quicktodo.data.repository

import com.xfei33.quicktodo.data.local.dao.FocusSessionDao
import com.xfei33.quicktodo.model.FocusSession
import javax.inject.Inject

class FocusSessionRepository @Inject constructor(
    private val focusSessionDao: FocusSessionDao
) {
    suspend fun insertFocusSession(focusSession: FocusSession) = focusSessionDao.insert(focusSession)

    fun getFocusSessionsByUserId(userId: Long) = focusSessionDao.getFocusSessionsByUserId(userId)

    suspend fun updateFocusSession(focusSession: FocusSession) = focusSessionDao.update(focusSession)

    fun getLastSessionByUserId(userId: Long) = focusSessionDao.getLastSessionByUserId(userId)
}