package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.local.dao.UserDao
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.data.remote.api.ApiService
import com.xfei33.quicktodo.data.remote.api.AuthRequest
import com.xfei33.quicktodo.data.repository.TodoRepository
import com.xfei33.quicktodo.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    val userPreferences: UserPreferences,
    private val todoRepository: TodoRepository,
    private val userDao: UserDao
) : ViewModel() {
    // 用户id
    private val _userId = MutableStateFlow<Long?>(null)
    val userId: StateFlow<Long?> get() = _userId

    // token
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> get() = _token

    // 同步状态
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> get() = _syncStatus

    sealed class SyncStatus {
        object Idle : SyncStatus()
        object Syncing : SyncStatus()
        object Success : SyncStatus()
        data class Failed(val message: String?) : SyncStatus()
    }

    init {
        viewModelScope.launch {
            val savedToken = userPreferences.token.firstOrNull()
            val savedUserId = userPreferences.userId.firstOrNull()
            _token.value = savedToken
            _userId.value = savedUserId
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
                    val errorMessage = parseErrorMessage(response.errorBody())
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
                    val token = "Bearer ${response.body()?.token.orEmpty()}"
                    val userId = response.body()?.userId ?: 0
                    userPreferences.saveToken(token)
                    userPreferences.saveUserId(userId)
                    userPreferences.saveIsFirstLaunch(false)
                    _token.value = token
                    _userId.value = userId

                    // 同步数据
                    _syncStatus.value = SyncStatus.Syncing
                    var syncResult: Result<Unit>
                    withContext(Dispatchers.IO) {
                        syncResult = todoRepository.syncWithServer()
                    }
                        if (syncResult.isSuccess) {
                            _syncStatus.value = SyncStatus.Success
                            onResult(true, "登录成功")
                        } else {
                            val errorMessage = syncResult.exceptionOrNull()?.message ?: "同步失败"
                            _syncStatus.value = SyncStatus.Failed(errorMessage)
                            onResult(false, errorMessage)
                        }
                } else {
                    val errorMessage = parseErrorMessage(response.errorBody())
                    _syncStatus.value = SyncStatus.Failed(errorMessage)
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = "网络错误：${e.message}"
                _syncStatus.value = SyncStatus.Failed(errorMessage)
                onResult(false, errorMessage)
            }
        }
    }

    // 离线使用
    fun offlineLogin() {
        viewModelScope.launch {
            userPreferences.saveIsFirstLaunch(false)
            userPreferences.saveUserId(0L)
            val user: User = User(0L)
            userDao.insertUser(user)
        }
    }

    // 登出
    fun logout() {
        viewModelScope.launch {
            userPreferences.clear()
            _userId.value = null
            _token.value = null
        }
    }

    // 解析错误消息
    private fun parseErrorMessage(errorBody: ResponseBody?): String {
        return errorBody?.string()?.let {
            try {
                val jsonObject = JSONObject(it)
                jsonObject.getString("error")
            } catch (e: Exception) {
                "未知错误"
            }
        } ?: "请求失败"
    }
}
