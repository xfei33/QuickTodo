package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import com.xfei33.quicktodo.ui.focus.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FocusViewModel @Inject constructor() : ViewModel() {
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
}