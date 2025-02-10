package com.xfei33.quicktodo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xfei33.quicktodo.data.dao.TodoDao
import com.xfei33.quicktodo.data.dao.UserDao
import com.xfei33.quicktodo.data.model.TodoItem
import com.xfei33.quicktodo.data.model.User
import javax.inject.Singleton

@Database(
    entities = [User::class, TodoItem::class], // 实体类列表
    version = 1, // 数据库版本
    exportSchema = false
)
@Singleton
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 单例模式获取数据库实例
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quicktodo_db" // 数据库名称
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}