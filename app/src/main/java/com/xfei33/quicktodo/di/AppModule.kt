package com.xfei33.quicktodo.di

import android.content.Context
import androidx.work.WorkManager
import com.xfei33.quicktodo.data.local.dao.MessageDao
import com.xfei33.quicktodo.data.local.dao.TodoDao
import com.xfei33.quicktodo.data.local.dao.UserDao
import com.xfei33.quicktodo.data.local.db.AppDatabase
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.data.remote.api.ApiService
import com.xfei33.quicktodo.data.remote.client.RetrofitClient
import com.xfei33.quicktodo.data.repository.MessageRepository
import com.xfei33.quicktodo.data.repository.TodoRepository
import com.xfei33.quicktodo.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
        fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideTodoDao(appDatabase: AppDatabase) = appDatabase.todoDao()

    @Provides
    fun provideMessageDao(appDatabase: AppDatabase) = appDatabase.messageDao()

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideTodoRepository(
        apiService: ApiService,
        userPreferences: UserPreferences,
        todoDao: TodoDao
    ): TodoRepository {
        return TodoRepository(todoDao, apiService, userPreferences)
    }

    @Provides
    @Singleton
    fun provideMessageRepository(
        messageDao: MessageDao
    ): MessageRepository {
        return MessageRepository(messageDao)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    fun provideUserDao(appDatabase: AppDatabase) = appDatabase.userDao()

    @Provides
    fun provideFocusSessionDao(appDatabase: AppDatabase) = appDatabase.focusSessionDao()

    @Provides
    fun provideUserRepository(userDao: UserDao) = UserRepository(userDao)
}