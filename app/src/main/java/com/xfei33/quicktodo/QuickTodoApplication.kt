package com.xfei33.quicktodo

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuickTodoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val todosChannel = NotificationChannel(
                "todos_channel",
                "待办任务通知",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "关于待办任务的提醒通知"
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(todosChannel)
        }
    }
}