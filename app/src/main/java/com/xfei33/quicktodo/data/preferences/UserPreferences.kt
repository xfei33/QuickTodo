package com.xfei33.quicktodo.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 创建 DataStore 实例
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USER_ID_KEY = longPreferencesKey("user_id")
        private val LAST_SYNC_TIME_KEY = stringPreferencesKey("lastSyncTime")

        private const val DEFAULT_TOKEN = "" // 离线用户默认token
        private const val DEFAULT_USER_ID = 0L // 离线用户默认id
        private const val DEFAULT_LAST_SYNC_TIME = "0001-01-01T00:00:00" // 离线用户默认lastSyncTime
    }

    // 保存 Token
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // 获取 Token
    val token: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: DEFAULT_TOKEN
        }

    // 保存 UserId
    suspend fun saveUserId(userId: Long) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    // 获取 UserId
    val userId: Flow<Long?>
        get() = context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY] ?: DEFAULT_USER_ID
        }

    // 保存 lastSyncTime
    suspend fun saveLastSyncTime(lastSyncTime: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIME_KEY] = lastSyncTime
        }
    }

    // 获取 lastSyncTime
    val lastSyncTime: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[LAST_SYNC_TIME_KEY]?: DEFAULT_LAST_SYNC_TIME
        }

    // 清除用户数据（用于注销）
    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(LAST_SYNC_TIME_KEY)
        }
    }

}
