package com.xfei33.quicktodo.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xfei33.quicktodo.data.local.converter.Converters
import com.xfei33.quicktodo.data.local.dao.MessageDao
import com.xfei33.quicktodo.data.local.dao.TodoDao
import com.xfei33.quicktodo.data.local.dao.UserDao
import com.xfei33.quicktodo.model.Message
import com.xfei33.quicktodo.model.Todo
import com.xfei33.quicktodo.model.User

@Database(
    entities = [Todo::class, Message::class, User::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao
    abstract fun messageDao(): MessageDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quicktodo_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
