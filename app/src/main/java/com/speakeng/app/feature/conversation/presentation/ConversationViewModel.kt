package com.speakeng.app.feature.conversation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.model.MessageSender
import com.speakeng.app.feature.conversation.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConversationUiState>(UiState.Loading)
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    init {
        // Phase 1: no persisted history yet, start from an empty conversation.
        _uiState.value = UiState.Success(emptyList())
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val currentMessages = (_uiState.value as? UiState.Success)?.data.orEmpty()
        val userMessage = ChatMessage(
            id = System.currentTimeMillis(),
            sender = MessageSender.USER,
            text = text,
            timestamp = System.currentTimeMillis(),
        )
        _uiState.value = UiState.Success(currentMessages + userMessage)

        viewModelScope.launch {
            val result = sendMessageUseCase(text)
            result.onSuccess { reply ->
                _uiState.update { state ->
                    val messages = (state as? UiState.Success)?.data.orEmpty()
                    UiState.Success(
                        messages + ChatMessage(
                            id = System.currentTimeMillis(),
                            sender = MessageSender.AI,
                            text = reply,
                            timestamp = System.currentTimeMillis(),
                        ),
                    )
                }
            }.onFailure { error ->
                _uiState.value = UiState.Error(error.message ?: "Failed to reach the AI conversation partner")
            }
        }
    }
}
