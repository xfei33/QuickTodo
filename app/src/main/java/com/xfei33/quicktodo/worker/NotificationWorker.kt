package com.xfei33.quicktodo.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xfei33.quicktodo.R
import com.xfei33.quicktodo.data.repository.MessageRepository
import com.xfei33.quicktodo.model.Message
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
        val todoId = inputData.getString(TODO_ID) ?: return Result.failure()
        val title = inputData.getString(TODO_TITLE) ?: "待办任务即将到期"
        val content = inputData.getString(TODO_CONTENT) ?: "您的任务“$title”将在3小时内到期。请及时完成。"

        // 发送通知
        sendNotification(
            title = "任务即将到期",
            content = "您的任务“$title”将在3小时内到期。请及时完成。"
        )

        // 创建消息
        val message = Message(
            iconResId = R.drawable.icon, // 使用默认图标资源ID
            title = "待办任务提醒",
            sender = "系统",
            content = "您的任务“$title”将在3小时内到期。请及时完成。",
            category = MessageCategory.NOTICE.ordinal,
            isRead = false,
            time = LocalDateTime.now()
        )
        messageRepository.insertMessage(message)

        return Result.success()
    }

    private fun sendNotification(title: String, content: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "todos_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(UUID.randomUUID().hashCode(), notification)
    }
}