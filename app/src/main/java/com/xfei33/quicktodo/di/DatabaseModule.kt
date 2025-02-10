package com.xfei33.quicktodo.di

import android.content.Context
import com.xfei33.quicktodo.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideTodoDao(database: AppDatabase) = database.todoDao()

    @Provides
    fun provideUserDao(database: AppDatabase) = database.userDao()
}