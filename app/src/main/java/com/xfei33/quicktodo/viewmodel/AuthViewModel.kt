package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.network.ApiService
import com.xfei33.quicktodo.network.AuthRequest
import com.xfei33.quicktodo.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.http.Body

class AuthViewModel : ViewModel() {
    private val apiService: ApiService = RetrofitClient.apiService

    fun register(username: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.register(AuthRequest(username, password))
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun login(username: String, password: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = apiService.login(AuthRequest(username, password))
                if (response.isSuccessful) {
                    onResult(response.body()?.token)
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
