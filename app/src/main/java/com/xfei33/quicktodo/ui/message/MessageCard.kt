package com.xfei33.quicktodo.ui.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xfei33.quicktodo.R
import com.xfei33.quicktodo.model.Message
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// 添加时间格式化函数
private fun formatMessageTime(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    return if (dateTime.toLocalDate() == now.toLocalDate()) {
        // 当天消息只显示时间
        dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    } else {
        // 非当天消息显示日期和时间
        dateTime.format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
    }
}

@Composable
fun MessageCard(
    message: Message,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Image(
                bitmap = message.icon,
                contentDescription = "消息图标",
                modifier = Modifier
                    .size(40.dp)
                    .clip(MaterialTheme.shapes.extraLarge)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatMessageTime(message.time),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                Text(
                    text = "来自：${message.sender}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    }
}

enum class MessageCategory {
    SYSTEM, // 系统消息
    ARTICLE, // 文章
    ACTIVITY, // 活动
    NOTICE // 通知
}

@Preview
@Composable
fun MessageCardPreview() {
    MessageCard(
        message = Message(
            icon = ImageBitmap.imageResource(R.drawable.icon),
            title = "标题",
            sender = "发送者",
            content = "内容",
            category = 0,
            isRead = false,
            time = LocalDateTime.now()
        )
    ) { /* 添加点击事件处理 */ }
}