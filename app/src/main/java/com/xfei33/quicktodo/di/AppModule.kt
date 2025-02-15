package com.xfei33.quicktodo.di

import android.content.Context
import com.xfei33.quicktodo.data.local.db.AppDatabase
import com.xfei33.quicktodo.data.preferences.UserPreferences
import com.xfei33.quicktodo.data.remote.api.ApiService
import com.xfei33.quicktodo.data.remote.client.RetrofitClient
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
    @Singleton
    fun provideApiService(): ApiService {
        return RetrofitClient.apiService
    }

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }
}