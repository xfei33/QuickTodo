package com.xfei33.quicktodo.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.xfei33.quicktodo.MainActivity
import com.xfei33.quicktodo.R
import com.xfei33.quicktodo.data.repository.FocusSessionRepository
import com.xfei33.quicktodo.notification.NotificationChannels
import com.xfei33.quicktodo.ui.focus.TimerState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerService : Service() {
    private val binder = TimerBinder()
    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    @Inject
    lateinit var focusSessionRepository: FocusSessionRepository

    private val _remainingSeconds = MutableStateFlow(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState: StateFlow<TimerState> = _timerState

    private var notificationManager: NotificationManager? = null

    inner class TimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
    }

    fun startTimer(totalSeconds: Long, isCountDown: Boolean, userId: Long) {
        serviceScope.launch {
            focusSessionRepository.startSession(userId)
            _timerState.value = TimerState.RUNNING
            _isRunning.value = true
            _remainingSeconds.value = if (isCountDown) totalSeconds else 0L

            while (_remainingSeconds.value > 0) {
                delay(1000)
                if (isCountDown) {
                    _remainingSeconds.value--
                    if (_remainingSeconds.value <= 0) {
                        _timerState.value = TimerState.IDLE
                        focusSessionRepository.stopSession()
                        break
                    }
                } else {
                    _remainingSeconds.value++
                }
                updateNotification()
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = TimerState.PAUSED
        _isRunning.value = false
    }

    fun resumeTimer(seconds: Long, isCountDown: Boolean) {
        startTimer(seconds, isCountDown, 0)
    }

    fun stopTimer(isNaturalEnd: Boolean = false) {
        timerJob?.cancel()
        _timerState.value = TimerState.IDLE
        _isRunning.value = false
        _remainingSeconds.value = 0
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        notificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)

        if (isNaturalEnd) {
            sendCompletionNotification()
        }
    }

    private fun createNotification() = NotificationCompat.Builder(this, NotificationChannels.TIMER_CHANNEL_ID)
        .setContentTitle("专注计时器")
        .setContentText(formatTime(_remainingSeconds.value))
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setOngoing(true)
        .setContentIntent(createPendingIntent())
        .build()

    private fun updateNotification() {
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun formatTime(seconds: Long): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    private fun sendCompletionNotification() {
        val notification = NotificationCompat.Builder(this, COMPLETION_CHANNEL_ID)
            .setContentTitle("计时完成")
            .setContentText("专注时间已结束！")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setSound(android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION))
            .setVibrate(longArrayOf(500, 500, 500))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(createPendingIntent())
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(COMPLETION_NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        private const val CHANNEL_ID = "timer_service_channel"
        private const val COMPLETION_CHANNEL_ID = "timer_completion_channel"
        private const val NOTIFICATION_ID = 1
        private const val COMPLETION_NOTIFICATION_ID = 2
    }
} 