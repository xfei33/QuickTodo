// 路径：ui/auth/AuthViewModel.kt
package com.xfei33.quicktodo.ui.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
    private val _username = mutableStateOf("")
    val username: State<String> = _username

    fun onLoginSuccess(username: String) {
        _username.value = username
    }

    /**
     * 登录逻辑
     * @param username 用户名
     * @param password 密码
     * @param onResult 登录结果回调（true 表示成功，false 表示失败）
     */
    fun login(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.login(username, password)
            onResult(success)
        }
    }

    /**
     * 注册逻辑
     * @param username 用户名
     * @param password 密码
     * @param onResult 注册结果回调（true 表示成功，false 表示失败）
     */
    fun register(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.register(username, password)
            onResult(success)
        }
    }
}