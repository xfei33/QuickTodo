package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.local.dao.FocusSessionDao
import com.xfei33.quicktodo.data.local.dao.TodoDao
import com.xfei33.quicktodo.data.local.dao.UserDao
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.data.repository.UserRepository
import com.xfei33.quicktodo.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDao: UserDao,
    private val todoDao: TodoDao,
    private val focusSessionDao: FocusSessionDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _focusData = MutableStateFlow(List(24) { 0 })
    val focusData: StateFlow<List<Int>> = _focusData

    private val _weeklyCompletedTasks = MutableStateFlow<List<Int>>(List(7) { 0 })
    val weeklyCompletedTasks: StateFlow<List<Int>> = _weeklyCompletedTasks

    init {
        viewModelScope.launch {
            val userId = userPreferences.userId.first()!!
            userRepository.getUserById(userId).collect { user ->
                _user.value = user
                loadFocusData(userId)
                getWeeklyCompletedTasks(userId)
            }
        }
    }

    private fun loadFocusData(userId: Long) {
        viewModelScope.launch {
            // 获取今天的开始时间和结束时间
            val today = LocalDate.now()
            val startOfDay = today.atStartOfDay()
            val endOfDay = today.plusDays(1).atStartOfDay()

            focusSessionDao.getFocusSessionsByUserIdAndTimeRange(
                userId,
                startOfDay,
                endOfDay
            ).collect { sessions ->
                val focusMinutes = MutableList(24) { 0 } // 每小时的专注分钟数
                if (sessions.isEmpty()) {
                    // 如果没有会话记录，直接更新 focusData
                    _focusData.value = focusMinutes
                    return@collect // 结束收集
                }
                sessions.forEach { session ->
                    val startTime = session.startTime
                    val endTime = session.endTime

                    // 计算每个小时的专注时间（分钟）
                    var currentHour = startTime
                    while (currentHour.isBefore(endTime)) {
                        val hourIndex = currentHour.hour
                        val minutesInThisHour = when {
                            currentHour.hour == startTime.hour && currentHour.hour == endTime.hour ->
                                endTime.minute - startTime.minute

                            currentHour.hour == startTime.hour ->
                                60 - startTime.minute

                            currentHour.hour == endTime.hour ->
                                endTime.minute

                            else ->
                                60
                        }
                        focusMinutes[hourIndex] += minutesInThisHour
                        currentHour = currentHour.plusHours(1)
                    }
                }
                _focusData.value = focusMinutes
            }
        }
    }

    fun getWeeklyCompletedTasks(userId: Long) {
        viewModelScope.launch {
            val today = LocalDate.now()
            val startOfWeek = today.minusDays(6) // 获取一周的开始日期
            val completedTasks = mutableListOf<Int>()

            for (day in 0..6) {
                val date = startOfWeek.plusDays(day.toLong())
                val record = todoDao.getTodayRecord(userId, date) // 获取当天的记录
                completedTasks.add(record?.completedCount ?: 0) // 如果没有记录则为0
            }
            _weeklyCompletedTasks.value = completedTasks // 更新状态流
        }
    }
} 