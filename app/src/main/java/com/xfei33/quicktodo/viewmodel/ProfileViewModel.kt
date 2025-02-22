package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.local.dao.FocusSessionDao
import com.xfei33.quicktodo.data.local.dao.UserDao
import com.xfei33.quicktodo.data.preferences.UserPreferences
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
    private val userDao: UserDao,
    private val focusSessionDao: FocusSessionDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _focusData = MutableStateFlow(List(24) { 0 })
    val focusData: StateFlow<List<Int>> = _focusData

    init {
        viewModelScope.launch {
            println("##################################################")
            val userId = userPreferences.userId.first()
            if (userId == 0L) {
                _user.value = User(id = userId)
            } else {
                _user.value = userDao.getUserById(userId!!).first()
            }
            loadFocusData(userId)
        }
    }

    private suspend fun loadFocusData(userId: Long) {
        // 获取今天的开始时间和结束时间
        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay()
        val endOfDay = today.plusDays(1).atStartOfDay()
        println("#############################startOfDay: $startOfDay, endOfDay: $endOfDay")

        focusSessionDao.getFocusSessionsByUserIdAndTimeRange(
            userId,
            startOfDay,
            endOfDay
        ).collect { sessions ->
            val focusMinutes = MutableList(24) { 0 } // 每小时的专注分钟数
            
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

                println("focusMinutes: $focusMinutes" + " session: $session")
            }
            _focusData.value = focusMinutes
        }
    }
} 