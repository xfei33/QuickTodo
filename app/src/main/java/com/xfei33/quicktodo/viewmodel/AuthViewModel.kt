package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.UserPreferences
import com.xfei33.quicktodo.network.ApiService
import com.xfei33.quicktodo.network.AuthRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {
//    private val apiService: ApiService = RetrofitClient.apiService

    // 登录状态
    private val _isLoggedIn = MutableStateFlow<Boolean>(false)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    // 用户id
    private val _userId = MutableStateFlow<Long>(0L)
    val userId: StateFlow<Long> get() = _userId

    // token
    private val _token = MutableStateFlow<String>("")
    val token: StateFlow<String?> get() = _token

    init {
        viewModelScope.launch {
            _token.value = userPreferences.token.toString()
            _userId.value = userPreferences.userId.first()!!
            if(_userId.value > 0) {
                _isLoggedIn.value = true
            }
        }
    }

    fun register(username: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.register(AuthRequest(username, password))
                if (response.isSuccessful) {
                    onResult(true, "注册成功")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null) {
                        val jsonObject = JSONObject(errorBody)
                        jsonObject.getString("error")
                    } else {
                        "注册失败"
                    }
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                onResult(false, "网络错误：${e.message}")
            }
        }
    }

    fun login(username: String, password: String, onResult: (Boolean?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.login(AuthRequest(username, password))
                if (response.isSuccessful) {
                    val token = "Bearer " + response.body()?.token
                    val userId = response.body()?.userId
                    userPreferences.saveToken(token)
                    userPreferences.saveUserId(userId!!)
                    _token.value = token
                    _userId.value = userId
                    _isLoggedIn.value = true
                    onResult(true)
                } else {
                    println("Login failed: ${response.errorBody()?.string()}")
                    onResult(false)
                }
            } catch (e: Exception) {
                println("Login error: ${e.message}")
                onResult(false)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.clear()
            _isLoggedIn.value = false
            _userId.value = 0L
            _token.value = ""
        }
    }
}


