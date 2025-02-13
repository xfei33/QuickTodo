package com.xfei33.quicktodo.data.di

import android.content.Context
import com.xfei33.quicktodo.data.dao.TodoDao
import com.xfei33.quicktodo.data.db.AppDatabase
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
    fun provideTodoDao(appDatabase: AppDatabase): TodoDao {
        return appDatabase.todoDao()
    }
}