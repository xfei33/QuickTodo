package com.xfei33.quicktodo.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val TODOS_CHANNEL_ID = "todos_channel"
    const val TIMER_CHANNEL_ID = "timer_service_channel"
    const val TIMER_COMPLETION_CHANNEL_ID = "timer_completion_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            
            // 待办任务通知通道
            val todosChannel = NotificationChannel(
                TODOS_CHANNEL_ID,
                "待办任务通知",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "关于待办任务的提醒通知"
                enableVibration(true)
                setVibrationPattern(longArrayOf(300, 500, 400))
            }

            // 计时器服务通道
            val timerServiceChannel = NotificationChannel(
                TIMER_CHANNEL_ID,
                "计时器服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "显示计时器运行状态"
            }

            // 计时完成通知通道
            val timerCompletionChannel = NotificationChannel(
                TIMER_COMPLETION_CHANNEL_ID,
                "计时完成通知",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "计时完成提醒"
                setSound(
                    android.media.RingtoneManager.getDefaultUri(android.media.RingtoneManager.TYPE_NOTIFICATION),
                    Notification.AUDIO_ATTRIBUTES_DEFAULT
                )
                enableVibration(true)
                vibrationPattern = longArrayOf(500, 500, 500)
            }

            // 创建所有通知通道
            notificationManager.createNotificationChannels(
                listOf(todosChannel, timerServiceChannel, timerCompletionChannel)
            )
        }
    }
} 