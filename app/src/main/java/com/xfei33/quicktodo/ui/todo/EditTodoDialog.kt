package com.xfei33.quicktodo.ui.todo

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.xfei33.quicktodo.model.Todo
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTodoDialog(
    todo: Todo,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, String, LocalDateTime, String) -> Unit,
) {
    var title by remember { mutableStateOf(todo.title) }
    var description by remember { mutableStateOf(todo.description) }
    var tag by remember { mutableStateOf(todo.tag) }
    var dueDate by remember { mutableStateOf(todo.dueDate) }
    var priority by remember { mutableStateOf(todo.priority) } // 默认优先级为“中”

    var isTimePickerDialogVisible by remember { mutableStateOf(false) }
    var isDatePickerDialogVisible by remember { mutableStateOf(false) }

    // 日期选择器状态
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dueDate.toEpochSecond(ZoneOffset.UTC) * 1000
    )

    // 时间选择器状态
    val timePickerState = rememberTimePickerState(
        initialHour = dueDate.hour,
        initialMinute = dueDate.minute,
        is24Hour = true
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
                    text = "编辑",
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
                    value = description?: "",
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 日期选择按钮
                    OutlinedButton(
                        onClick = {
                            isDatePickerDialogVisible = true
                        }
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = "选择日期")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("选择日期")
                    }

                    // 时间选择按钮
//                    OutlinedButton(
//                        onClick = {
//                            isTimePickerDialogVisible = true
//                        }
//                    ) {
//                        Icon(painterResource(R.drawable.baseline_access_time_24), contentDescription = "选择时间")
//                        Spacer(modifier = Modifier.width(4.dp))
//                        Text("选择时间")
//                    }
                }

                // 优先级选择
                Text(
                    text = "优先级",
                    style = MaterialTheme.typography.labelLarge
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    val options = listOf("低", "中", "高")
                    options.forEach { option ->
                        val selected = when (priority) {
                            "LOW" -> option == "低"
                            "MEDIUM" -> option == "中"
                            "HIGH" -> option == "高"
                            else -> false
                        }
                        SegmentedButton(
                            selected = selected,
                            onClick = {
                                priority = when (option) {
                                    "低" -> "LOW"
                                    "中" -> "MEDIUM"
                                    "高" -> "HIGH"
                                    else -> priority
                                }
                            },
                            modifier = Modifier
                                .width(0.dp)
                                .weight(1f)
                                .padding(horizontal = 4.dp) // 添加按钮之间的间距
                                .animateContentSize(), // 添加动画效果
                            shape = MaterialTheme.shapes.small, // 设置按钮形状
                            colors = SegmentedButtonDefaults.colors(
                                activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                inactiveContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(
                                text = option,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
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
                        onConfirm(title, description, tag, dueDate, priority?: "MEDIUM")
                    }) {
                        Text("确定")
                    }
                }

                if (isTimePickerDialogVisible) {
                    TimePickerDialog(
                        onDismiss = {isTimePickerDialogVisible = false},
                        onConfirm = {
                            dueDate = LocalDateTime.of(
                                dueDate.toLocalDate(),
                                LocalTime.of(timePickerState.hour, timePickerState.minute)
                            )
                            isTimePickerDialogVisible = false
                        }
                    ) {
                        TimePicker(state = timePickerState)
                    }
                }

                if (isDatePickerDialogVisible) {
                    DatePickerDialog(
                        onDismissRequest = { isDatePickerDialogVisible = false },
                        confirmButton = {
                            TextButton(onClick = {
                                val selectedDate = Instant.ofEpochMilli(datePickerState.selectedDateMillis!!)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                                dueDate = LocalDateTime.of(selectedDate, dueDate.toLocalTime())
                                isDatePickerDialogVisible = false
                                isTimePickerDialogVisible = true
                            }) {
                                Text("确定")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = onDismiss) {
                                Text("取消")
                            }
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }
            }
        }
    }
}

//@Composable
//fun PriorityButton(
//    text: String,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    Button(
//        onClick = onClick,
//        colors = ButtonDefaults.buttonColors(
//            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
//        ),
//        modifier = Modifier.fillMaxWidth(1f)// 确保每个按钮宽度相同
//    ) {
//        Text(text)
//    }
//}
//
//@Composable
//fun TimePickerDialog(
//    onDismiss: () -> Unit,
//    onConfirm: () -> Unit,
//    content: @Composable () -> Unit
//) {
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        dismissButton = {
//            TextButton (onClick = { onDismiss() }) {
//                Text("Dismiss")
//            }
//        },
//        confirmButton = {
//            TextButton(onClick = { onConfirm() }) {
//                Text("OK")
//            }
//        },
//        text = { content() }
//    )
//}
