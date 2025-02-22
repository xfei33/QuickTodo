package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.data.repository.FocusSessionRepository
import com.xfei33.quicktodo.model.FocusSession
import com.xfei33.quicktodo.ui.focus.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor(
    private val focusSessionRepository: FocusSessionRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {
    private val _focusSessions = MutableStateFlow<List<FocusSession>>(emptyList())
    val focusSessions: StateFlow<List<FocusSession>> = _focusSessions

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState

    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds

    private val _selectedMinutes = MutableStateFlow(25f)
    val selectedMinutes: StateFlow<Float> = _selectedMinutes

    private val _isCountDown = MutableStateFlow(true)
    val isCountDown: StateFlow<Boolean> = _isCountDown

    private val _pausedSeconds = MutableStateFlow(0L)
    val pausedSeconds: StateFlow<Long> = _pausedSeconds

    private val _startTime = MutableStateFlow<LocalDateTime?>(null)
    val startTime: StateFlow<LocalDateTime?> = _startTime

    private val _stopTime = MutableStateFlow<LocalDateTime?>(null)

    val _userId = MutableStateFlow<Long?>(0L)
    val userId: StateFlow<Long?> = _userId

    init {
        viewModelScope.launch {
            val userId = userPreferences.userId.first()
            _userId.value = userId
        }
    }

    fun updateTimerState(state: TimerState) {
        _timerState.value = state
    }

    fun updateRemainingSeconds(seconds: Long) {
        _remainingSeconds.value = seconds
    }

    fun updateSelectedMinutes(minutes: Float) {
        _selectedMinutes.value = minutes
    }

    fun updateIsCountDown(isCountDown: Boolean) {
        _isCountDown.value = isCountDown
    }

    fun updatePausedSeconds(seconds: Long) {
        _pausedSeconds.value = seconds
    }

    fun startFocusSession(userId: Long) {
        viewModelScope.launch {
            focusSessionRepository.startSession(userId)
        }
    }

    fun stopFocusSession() {
        viewModelScope.launch {
            focusSessionRepository.stopSession()
        }
    }

}