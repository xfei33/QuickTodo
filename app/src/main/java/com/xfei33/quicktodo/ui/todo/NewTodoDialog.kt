package com.xfei33.quicktodo.ui.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.xfei33.quicktodo.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun NewTodoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String, LocalDateTime, String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(LocalDateTime.now()) }
    var priority by remember { mutableStateOf("中") } // 默认优先级为“中”
    val context = LocalContext.current

    // 日期选择器
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            val selectedDate = LocalDate.of(year, month + 1, day)
            dueDate = LocalDateTime.of(selectedDate, dueDate.toLocalTime())
        },
        dueDate.year,
        dueDate.monthValue - 1,
        dueDate.dayOfMonth
    )


    // 时间选择器
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val selectedTime = LocalTime.of(hour, minute)
            dueDate = LocalDateTime.of(dueDate.toLocalDate(), selectedTime)
        },
        dueDate.hour,
        dueDate.minute,
        true
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "新建",
                    style = MaterialTheme.typography.titleLarge
                )

                // 标题输入
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 描述输入
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("描述") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                // 标签输入
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("标签") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // 日期和时间选择
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "选择日期")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("选择日期")
                    }
                    Button(onClick = { timePickerDialog.show() }) {
                        Icon(painterResource(id = R.drawable.baseline_access_time_24), contentDescription = "选择时间")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("选择时间")
                    }
                }

                // 优先级选择
                Text(
                    text = "优先级",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PriorityButton("低", priority == "低") { priority = "LOW" }
                    PriorityButton("中", priority == "中") { priority = "MEDIUM" }
                    PriorityButton("高", priority == "高") { priority = "HIGH" }
                }

                // 确定和取消按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    TextButton(onClick = {
                        onConfirm(title, description, tag, dueDate, priority)
                    }) {
                        Text("确定")
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(text)
    }
}
