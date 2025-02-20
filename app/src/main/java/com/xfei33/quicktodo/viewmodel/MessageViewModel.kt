package com.xfei33.quicktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xfei33.quicktodo.data.repository.MessageRepository
import com.xfei33.quicktodo.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    private val messageRepository: MessageRepository
) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _currentMessage = MutableStateFlow<Message?>(null)
    val currentMessage: StateFlow<Message?> = _currentMessage

    init {
        viewModelScope.launch {
            messageRepository.getAllMessages().collectLatest { messages ->
                _messages.value = messages
            }
        }
    }

    fun getMessageById(messageId: String) {
        viewModelScope.launch {
            messageRepository.getMessageById(messageId)?.let { message ->
                _currentMessage.value = message
            }
        }
    }

    fun markMessageAsRead(messageId: String) {
        viewModelScope.launch {
            messageRepository.getMessageById(messageId)?.let { message ->
                if (!message.isRead) {
                    messageRepository.updateMessage(message.copy(isRead = true))
                    _currentMessage.value = message.copy(isRead = true)
                }
            }
        }
    }
} 