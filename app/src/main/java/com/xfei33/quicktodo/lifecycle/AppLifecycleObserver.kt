package com.xfei33.quicktodo.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.xfei33.quicktodo.data.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppLifecycleObserver(
    private val todoRepository: TodoRepository,
) : DefaultLifecycleObserver {

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        syncData()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        syncData()
    }

    private fun syncData() {
        CoroutineScope(Dispatchers.IO).launch {
            todoRepository.syncWithServer()
        }
    }
}
