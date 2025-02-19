package com.xfei33.quicktodo.data.repository

import com.xfei33.quicktodo.data.local.dao.MessageDao
import com.xfei33.quicktodo.model.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val messageDao: MessageDao
) {
    fun getAllMessages(): Flow<List<Message>> = messageDao.getAllMessages()
    
    suspend fun getMessageById(id: String): Message? = messageDao.getMessageById(id)
    
    suspend fun updateMessage(message: Message) = messageDao.updateMessage(message)
    
    suspend fun insertMessage(message: Message) = messageDao.insertMessage(message)
}