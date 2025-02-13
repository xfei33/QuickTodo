package com.xfei33.quicktodo.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.xfei33.quicktodo.viewmodel.AuthViewModel

@Composable
fun AuthScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val authViewModel: AuthViewModel = viewModel()

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
                authViewModel.login(username, password) { token ->
                    if (token != null) {
                        // 登录成功，跳转到待办页面
                        navController.navigate("todo")
                    } else {
                        errorMessage = "登录失败，请检查用户名和密码"
                    }
                }
            } else {
                authViewModel.register(username, password) { success ->
                    if (success) {
                        errorMessage = "注册成功，请登录"
                        isLogin = true
                    } else {
                        errorMessage = "注册失败，请重试"
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
        TextButton(onClick = { navController.navigate("todo") }) {
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
