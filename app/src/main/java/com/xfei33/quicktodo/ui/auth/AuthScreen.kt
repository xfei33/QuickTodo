package com.xfei33.quicktodo.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xfei33.quicktodo.viewmodel.AuthViewModel

@Composable
fun AuthScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val authViewModel: AuthViewModel = hiltViewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLogin) "登录" else "注册",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (isLogin) {
                authViewModel.login(username, password) { isLogin ->
                    if (isLogin == true) {
                        // 登录成功，跳转到主界面
                        navController.navigate("main") {
                            popUpTo("auth") { inclusive = true } // 清除导航栈
                        }
                    } else {
                        errorMessage = "登录失败，请检查用户名和密码"
                    }
                }
            } else {
                authViewModel.register(username, password) { success, message ->
                    if (success) {
                        // 注册成功后自动登录
                        authViewModel.login(username, password) { isLogin ->
                            if (isLogin == true) {
                                // 登录成功，跳转到主界面
                                navController.navigate("main") {
                                    popUpTo("auth") { inclusive = true } // 清除导航栈
                                }
                            } else {
                                errorMessage = "注册成功，但自动登录失败"
                            }
                        }
                    } else {
                        errorMessage = message
                    }
                }
            }
        }) {
            Text(if (isLogin) "登录" else "注册")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { isLogin = !isLogin }) {
            Text(if (isLogin) "没有账号？注册" else "已有账号？登录")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("main") }) {
            Text("离线使用")
        }
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}