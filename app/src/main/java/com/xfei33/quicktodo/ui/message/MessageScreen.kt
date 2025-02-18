package com.xfei33.quicktodo.ui.message

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xfei33.quicktodo.R
import com.xfei33.quicktodo.model.Message
import java.time.LocalDateTime


@Composable
fun MessageScreen(
    onMessageClick: (Message) -> Unit = {}
) {
    val messagesMock = listOf(
        Message(
            icon = ImageBitmap.imageResource(R.drawable.icon),
            title = "系统通知",
            content = "新版本已发布，请及时更新",
            time = LocalDateTime.parse("2025-02-18T12:00:00"),
            isRead = false,
            category = MessageCategory.SYSTEM.ordinal,
            sender = "System"
        ),
        Message(
            icon = ImageBitmap.imageResource(R.drawable.icon),
            title = "待办提醒",
            content = "您有3个待办事项即将到期",
            time = LocalDateTime.parse("2025-02-01T13:00:00"),
            isRead = true,
            category = MessageCategory.NOTICE.ordinal,
            sender = "System"
        ),
        Message(
            icon = ImageBitmap.imageResource(R.drawable.icon),
            title = "活动通知",
            content = "恭喜您获得成就",
            time = LocalDateTime.parse("2025-02-01T13:00:00"),
            isRead = true,
            category = MessageCategory.ACTIVITY.ordinal,
            sender = "System"
        )
    )
    Scaffold(
        topBar = {
            MassageTopBar()
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding).padding(top = 16.dp)
        ) {
            items(messagesMock) { message ->
                MessageCard(
                    message = message,
                    onClick = { onMessageClick(message) }
                )
            }
        }
    }
}

@Preview
@Composable
fun MessageScreenPreview() {
    MessageScreen()
}