package com.xfei33.quicktodo

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xfei33.quicktodo.data.repository.TodoRepository
import com.xfei33.quicktodo.lifecycle.AppLifecycleObserver
import com.xfei33.quicktodo.navigation.NavRoutes
import com.xfei33.quicktodo.ui.auth.AuthScreen
import com.xfei33.quicktodo.ui.main.MainScreen
import com.xfei33.quicktodo.ui.splash.SplashScreen
import com.xfei33.quicktodo.ui.theme.QuickTodoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var todoRepository: TodoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 注册LifecycleObserver
        lifecycle.addObserver(AppLifecycleObserver(todoRepository))

        // 检查并请求通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        enableEdgeToEdge()
        setContent {
            QuickTodoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuickTodoApp()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        when {
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED -> {
                // 已经有权限，不需要做任何事
            }
            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                // 用户之前拒绝过，显示解释对话框
                AlertDialog.Builder(this)
                    .setTitle("需要通知权限")
                    .setMessage("为了显示计时器状态，应用需要通知权限。请在设置中开启通知权限。")
                    .setPositiveButton("去设置") { _, _ ->
                        // 打开应用设置页面
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", packageName, null)
                            startActivity(this)
                        }
                    }
                    .setNegativeButton("取消", null)
                    .show()
            }
            else -> {
                // 首次请求权限
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                    if (!isGranted) {
                        // 用户拒绝了权限，显示提示信息
                        Toast.makeText(this, "没有通知权限，计时器可能无法正常工作", Toast.LENGTH_LONG).show()
                    }
                }.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun QuickTodoApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(NavRoutes.SPLASH) { SplashScreen(navController) }
        composable(NavRoutes.AUTH) { AuthScreen(navController) }
        composable(NavRoutes.MAIN) { MainScreen() }
    }
}
