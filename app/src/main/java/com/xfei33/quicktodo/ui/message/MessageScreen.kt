package com.xfei33.quicktodo.ui.message

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xfei33.quicktodo.model.Message
import com.xfei33.quicktodo.viewmodel.MessageViewModel

@Composable
fun MessageScreen(
    onMessageClick: (Message) -> Unit = {},
    viewModel: MessageViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    
    Scaffold(
        topBar = {
            MessageTopBar()
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(top = 16.dp)
        ) {
            items(messages) { message ->
                MessageCard(
                    message = message,
                    onClick = { onMessageClick(message) }
                )
            }
        }
    }
}