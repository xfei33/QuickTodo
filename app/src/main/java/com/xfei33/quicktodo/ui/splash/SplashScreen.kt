package com.xfei33.quicktodo.ui.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xfei33.quicktodo.R
import com.xfei33.quicktodo.ui.theme.QuickTodoTheme
import com.xfei33.quicktodo.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, authViewModel: AuthViewModel = hiltViewModel()) {
    val isFirstLaunch by authViewModel.userPreferences.isFirstLaunch.collectAsState(initial = true)

    // 检查登录状态并跳转
    LaunchedEffect(isFirstLaunch) {
        delay(500)
        if (isFirstLaunch) {
            navController.navigate("auth") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            navController.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    SplashContent()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashContent() {
    Box(
        modifier = Modifier.fillMaxSize()
        .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 应用 Logo
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "App Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(100.dp),
            )

            // 应用名称
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )

            // 加载动画
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSplashContent() {
    QuickTodoTheme {
        SplashContent()
    }
}
