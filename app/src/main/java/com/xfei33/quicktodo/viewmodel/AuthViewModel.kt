package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.data.remote.api.ApiService
import com.xfei33.quicktodo.data.remote.api.AuthRequest
import com.xfei33.quicktodo.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences,
    private val todoRepository: TodoRepository
) : ViewModel() {
    // 登录状态
    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    // 用户id
    private val _userId = MutableStateFlow<Long?>(null)
    val userId: StateFlow<Long?> get() = _userId

    // token
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> get() = _token

    init {
        viewModelScope.launch {
            // 使用firstOrNull防止当Flow中没有数据时抛出异常
            val savedToken = userPreferences.token
            val savedUserId = userPreferences.userId.firstOrNull()
            _token.value = savedToken.first()
            _userId.value = savedUserId
            _isLoggedIn.value = savedUserId != null && savedUserId > 0
        }
    }

    // 注册
    fun register(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.register(AuthRequest(username, password))
                if (response.isSuccessful) {
                    onResult(true, "注册成功")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let {
                        val jsonObject = JSONObject(it)
                        jsonObject.getString("error")
                    } ?: "注册失败"
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                onResult(false, "网络错误：${e.message}")
            }
        }
    }

    // 登录
    fun login(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.login(AuthRequest(username, password))
                if (response.isSuccessful) {
                    val token = "Bearer " + response.body()?.token.orEmpty()
                    val userId = response.body()?.userId ?: 0
                    userPreferences.saveToken(token)
                    userPreferences.saveUserId(userId)
                    _token.value = token
                    _userId.value = userId
                    _isLoggedIn.value = true
                    println("#################################################")
                    // 同步数据
                    todoRepository.syncWithServer()
                    onResult(true, "登录成功")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let {
                        val jsonObject = JSONObject(it)
                        jsonObject.getString("error")
                    } ?: "登录失败"
                    println("登录失败: $errorMessage")
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "网络错误：${e.message}"
                println("登录错误: $errorMessage")
                onResult(false, errorMessage)
            }
        }
    }

    // 登出
    fun logout() {
        viewModelScope.launch {
            userPreferences.clear()
            _isLoggedIn.value = false
            _userId.value = null
            _token.value = null
        }
    }
}
