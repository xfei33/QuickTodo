// 路径：ui/auth/RegisterScreen.kt
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

@Composable
fun RegisterScreen(
    onRegisterSuccess: (username: String) -> Unit, // 注册成功后的回调
    onNavigateToLogin: () -> Unit, // 跳转到登录页面的回调
    viewModel: AuthViewModel = hiltViewModel() // 使用 Hilt 注入 ViewModel
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) } // 错误提示信息

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // 用户名输入框
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 密码输入框
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 确认密码输入框
        TextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("确认密码") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null
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

        // 注册按钮
        Button(
            onClick = {
                if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    errorMessage = "用户名和密码不能为空"
                } else if (password != confirmPassword) {
                    errorMessage = "两次输入的密码不一致"
                } else {
                    viewModel.register(username, password) { success ->
                        if (success) {
                            errorMessage = null
                            onRegisterSuccess(username) // 注册成功，跳转到主页面
                        } else {
                            errorMessage = "用户名已存在"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("注册")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 返回登录按钮
        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("返回登录")
        }
    }
}