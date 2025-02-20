package com.xfei33.quicktodo.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xfei33.quicktodo.MainActivity
import com.xfei33.quicktodo.R
import com.xfei33.quicktodo.data.repository.MessageRepository
import com.xfei33.quicktodo.model.Message
import com.xfei33.quicktodo.notification.NotificationChannels
import com.xfei33.quicktodo.ui.message.MessageCategory
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime
import java.util.UUID

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val messageRepository: MessageRepository
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val TODO_ID = "todo_id"
        const val TODO_TITLE = "todo_title"
        const val TODO_CONTENT = "todo_content"
    }

    override suspend fun doWork(): Result {
        val title = inputData.getString(TODO_TITLE) ?: "待办任务即将到期"

        // 发送通知
        sendNotification(
            title = "任务即将到期",
            content = "您的任务“$title”将在1小时内到期。请及时完成。"
        )

        // 创建消息
        val message = Message(
            iconResId = R.drawable.icon,
            title = "待办临期提醒",
            sender = "系统",
            content = "您的任务“$title”将在1小时内到期。请及时完成。",
            category = MessageCategory.NOTICE.ordinal,
            isRead = false,
            time = LocalDateTime.now()
        )
        messageRepository.insertMessage(message)

        return Result.success()
    }

    private fun sendNotification(title: String, content: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 创建打开MainActivity的Intent
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, NotificationChannels.TODOS_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // 设置点击通知时的Intent
            .build()

        notificationManager.notify(UUID.randomUUID().hashCode(), notification)
    }
}