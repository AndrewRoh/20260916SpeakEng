package com.speakeng.app.feature.conversation.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.ui.components.UiStateContent
import com.speakeng.app.core.ui.theme.SpeakEngTheme
import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.model.MessageSender

@Composable
fun ConversationScreen(viewModel: ConversationViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { messages ->
        ConversationContent(messages = messages, onSend = viewModel::sendMessage)
    }
}

@Composable
private fun ConversationContent(messages: List<ChatMessage>, onSend: (String) -> Unit) {
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages, key = { it.id }) { message ->
                val label = if (message.sender == MessageSender.USER) "You" else "AI"
                Text(text = "$label: ${message.text}", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                label = { Text("Type a message") },
            )
            Button(
                onClick = { onSend(input); input = "" },
                modifier = Modifier.padding(start = 8.dp),
            ) {
                Text("Send")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConversationScreenPreview() {
    SpeakEngTheme {
        UiStateContent(
            uiState = UiState.Success(
                listOf(
                    ChatMessage(1, MessageSender.AI, "Hi! What did you do today?", 0L),
                    ChatMessage(2, MessageSender.USER, "I went to the market.", 0L),
                ),
            ),
        ) { ConversationContent(it, onSend = {}) }
    }
}
