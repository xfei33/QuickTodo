package com.xfei33.quicktodo.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xfei33.quicktodo.viewmodel.AuthViewModel

@Composable
fun AuthScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val syncStatus by authViewModel.syncStatus.collectAsState()

    when (syncStatus) {
        is AuthViewModel.SyncStatus.Syncing -> {
            // 显示加载指示器
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
        }

        is AuthViewModel.SyncStatus.Success -> {
            // 跳转到主界面
            navController.navigate("main")
        }

        is AuthViewModel.SyncStatus.Failed -> {
            // 显示错误提示
            Text(text = "同步失败：${(syncStatus as AuthViewModel.SyncStatus.Failed).message}")
        }

        else -> {
            // 显示登录/注册界面
            AuthContent(
                username = username,
                onUsernameChange = { username = it },
                password = password,
                onPasswordChange = { password = it },
                isLogin = isLogin,
                onToggleAuthMode = { isLogin = !isLogin },
                errorMessage = errorMessage,
                onLogin = {
                    authViewModel.login(username, password) { success, message ->
                        if (success) {
                            navController.navigate("main") {
                                popUpTo("auth") { inclusive = true }
                            }
                        } else {
                            errorMessage = message
                        }
                    }
                },
                onRegister = {
                    authViewModel.register(username, password) { success, message ->
                        if (success) {
                            authViewModel.login(username, password) { loginSuccess, loginMessage ->
                                if (loginSuccess) {
                                    navController.navigate("main") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                } else {
                                    errorMessage = loginMessage
                                }
                            }
                        } else {
                            errorMessage = message
                        }
                    }
                },
                onOfflineUse = {
                    authViewModel.offlineLogin()
                    // 跳转到主界面
                    navController.navigate("main") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun AuthContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLogin: Boolean,
    onToggleAuthMode: () -> Unit,
    errorMessage: String?,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onOfflineUse: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // Increased padding for better spacing
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isLogin) "登录" else "注册",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), // Bold heading
            color = MaterialTheme.colorScheme.primary // Color for title
        )

        Spacer(modifier = Modifier.height(32.dp)) // Increased space between title and input fields

        // Username field with clear accent color
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("用户名") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing

        // Password field with secure input and styled border
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("密码") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp)) // More space before button

        // Button with elevated effect
        ElevatedButton(
            onClick = if (isLogin) onLogin else onRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) "登录" else "注册")
        }

        Spacer(modifier = Modifier.height(16.dp)) // Space before toggle button

        // Toggle Auth mode with a secondary style
        TextButton(
            onClick = onToggleAuthMode,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLogin) "没有账号？注册" else "已有账号？登录")
        }

        Spacer(modifier = Modifier.height(8.dp)) // Smaller space for offline button

        // Offline use button with subtle style
        TextButton(
            onClick = onOfflineUse,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("离线使用")
        }

        // Error message with a red accent color
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp)) // Space above error message
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) // Emphasized error message
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun PreviewAuthContent() {
    MaterialTheme {
        AuthContent(
            username = "testuser",
            onUsernameChange = {},
            password = "password",
            onPasswordChange = {},
            isLogin = true,
            onToggleAuthMode = {},
            errorMessage = null,
            onLogin = {},
            onRegister = {},
            onOfflineUse = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAuthContentWithError() {
    MaterialTheme {
        AuthContent(
            username = "testuser",
            onUsernameChange = {},
            password = "password",
            onPasswordChange = {},
            isLogin = false,
            onToggleAuthMode = {},
            errorMessage = "用户名或密码错误",
            onLogin = {},
            onRegister = {},
            onOfflineUse = {}
        )
    }
}

