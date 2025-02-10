package com.xfei33.quicktodo

import android.app.Application
import com.xfei33.quicktodo.data.db.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class QuickTodoApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
}