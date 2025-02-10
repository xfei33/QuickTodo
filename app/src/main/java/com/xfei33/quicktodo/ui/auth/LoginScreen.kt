// 路径：ui/auth/LoginScreen.kt
package com.xfei33.quicktodo.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xfei33.quicktodo.ui.todos.TodoViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit, // 登录成功后的回调
    onNavigateToRegister: () -> Unit, // 跳转到注册页面的回调
    viewModel: AuthViewModel = hiltViewModel(),
    todoViewModel: TodoViewModel = hiltViewModel() // 注入 TodoViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        // 用户名输入框
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 密码输入框
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation()
        )

        // 错误提示
        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 登录按钮
        Button(
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    errorMessage = "用户名和密码不能为空"
                } else {
                    viewModel.login(username, password) { success ->
                        if (success) {
                            errorMessage = null
                            todoViewModel.setUsername(username) // 设置用户名
                            onLoginSuccess() // 登录成功，调用回调
                        } else {
                            errorMessage = "用户名或密码错误"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("登录")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 注册按钮
        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("注册新账户")
        }
    }
}