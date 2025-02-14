package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.network.ApiService
import com.xfei33.quicktodo.network.AuthRequest
import com.xfei33.quicktodo.network.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val apiService: ApiService = RetrofitClient.apiService
    var token: String? = null
    var userId: Long = 0
    var isLoggedIn: Boolean = false

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

    fun login(username: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.login(AuthRequest(username, password))
                if (response.isSuccessful) {
                    isLoggedIn = true
                    token = response.body()?.token
                    userId = response.body()?.userId?: 0
                    onResult(token)
                } else {
                    println("Login failed: ${response.errorBody()?.string()}")
                    onResult(null)
                }
            } catch (e: Exception) {
                println("Login error: ${e.message}")
                onResult(null)
            }
        }
    }
}


