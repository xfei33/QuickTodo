package com.xfei33.quicktodo.data.repository

import com.xfei33.quicktodo.data.local.dao.FocusSessionDao
import com.xfei33.quicktodo.data.local.dao.UserDao
import com.xfei33.quicktodo.model.FocusSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusSessionRepository @Inject constructor(
    private val focusSessionDao: FocusSessionDao,
    private val userDao: UserDao
) {
    private val _currentSession = MutableStateFlow<FocusSession?>(null)
    val currentSession = _currentSession.asStateFlow()

    suspend fun startSession(userId: Long) {
        val session = FocusSession(
            userId = userId,
            startTime = LocalDateTime.now(),
            endTime = LocalDateTime.now(), // 初始值，结束时间将在停止时更新
            duration = 0L
        )
        focusSessionDao.insert(session)
        _currentSession.value = session
    }

    suspend fun stopSession() {
        _currentSession.value?.let { session ->
            val updatedSession = session.copy(
                endTime = LocalDateTime.now(),
                duration = Duration.between(session.startTime, LocalDateTime.now()).toMillis()
            )
            focusSessionDao.update(updatedSession) // 或者使用 update 方法
            userDao.addCredits(updatedSession.userId, (updatedSession.duration / 60000).toInt())
            _currentSession.value = null
        }
    }

    fun getFocusSessionsByUserId(userId: Long): Flow<List<FocusSession>> {
        return focusSessionDao.getFocusSessionsByUserId(userId)
    }

    suspend fun updateCreditsForFocusTime(userId: Long, minutes: Int) {
        userDao.addCredits(userId, minutes)
    }
}