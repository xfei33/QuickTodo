package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.local.dao.UserDao
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    init {
        viewModelScope.launch {
            val userId = userPreferences.userId.first()
            userDao.getUserById(userId!!).collect { user ->
                if (userId == 0L) {
                    // 离线用户，使用默认用户对象
                    _user.value = User(id = userId)
                } else {
                    _user.value = user ?: User(id = userId)
                }
            }
        }
    }
} 