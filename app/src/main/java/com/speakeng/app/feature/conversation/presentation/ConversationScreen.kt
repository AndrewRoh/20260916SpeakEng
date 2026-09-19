package com.speakeng.app.feature.conversation.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import com.speakeng.app.core.ui.components.PlaybackControlBar
import com.speakeng.app.core.ui.components.UiStateContent
import com.speakeng.app.core.ui.theme.SpeakEngTheme
import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.model.MessageSender

@Composable
fun ConversationScreen(viewModel: ConversationViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    UiStateContent(uiState = uiState) { data ->
        ConversationContent(
            data = data,
            onSend = viewModel::sendMessage,
            onListen = viewModel::listenToMessage,
            onStopListening = viewModel::stopListening,
            onRateSelected = viewModel::setPlaybackRate,
            onToggleRepeat = viewModel::toggleRepeat,
            onToggleAutoPlay = viewModel::toggleAutoPlay,
            onDismissError = viewModel::dismissError,
        )
    }
}

@Composable
private fun ConversationContent(
    data: ConversationData,
    onSend: (String) -> Unit,
    onListen: (Long) -> Unit,
    onStopListening: () -> Unit,
    onRateSelected: (Float) -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleAutoPlay: () -> Unit,
    onDismissError: () -> Unit,
) {
    var input by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Auto-play AI replies")
            Switch(checked = data.isAutoPlayEnabled, onCheckedChange = { onToggleAutoPlay() })
        }

        data.errorMessage?.let { message ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onDismissError) {
                    Icon(Icons.Filled.Close, contentDescription = "Dismiss error")
                }
            }
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(data.messages, key = { it.id }) { message ->
                MessageRow(
                    message = message,
                    isPlaying = message.id == data.playingMessageId,
                    onListen = { onListen(message.id) },
                    onStopListening = onStopListening,
                )
            }
        }

        val playingMessageId = data.playingMessageId
        if (playingMessageId != null) {
            PlaybackControlBar(
                isPlaying = true,
                rate = data.rate,
                isRepeatEnabled = data.isRepeatEnabled,
                onPlay = { onListen(playingMessageId) },
                onStop = onStopListening,
                onRateSelected = onRateSelected,
                onToggleRepeat = onToggleRepeat,
            )
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

@Composable
private fun MessageRow(
    message: ChatMessage,
    isPlaying: Boolean,
    onListen: () -> Unit,
    onStopListening: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val label = if (message.sender == MessageSender.USER) "You" else "AI"
        Text(text = "$label: ${message.text}", modifier = Modifier.weight(1f))
        if (message.sender == MessageSender.AI) {
            IconButton(onClick = if (isPlaying) onStopListening else onListen) {
                Icon(
                    if (isPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                    contentDescription = if (isPlaying) "Stop" else "Listen",
                )
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
                ConversationData(
                    messages = listOf(
                        ChatMessage(1, MessageSender.AI, "Hi! What did you do today?", 0L),
                        ChatMessage(2, MessageSender.USER, "I went to the market.", 0L),
                    ),
                    playingMessageId = null,
                    rate = 1f,
                    isRepeatEnabled = false,
                    isAutoPlayEnabled = false,
                    errorMessage = null,
                ),
            ),
        ) {
            ConversationContent(
                data = it,
                onSend = {},
                onListen = {},
                onStopListening = {},
                onRateSelected = {},
                onToggleRepeat = {},
                onToggleAutoPlay = {},
                onDismissError = {},
            )
        }
    }
}
