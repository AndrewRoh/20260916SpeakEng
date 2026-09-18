package com.speakeng.app.feature.conversation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.model.MessageSender
import com.speakeng.app.feature.conversation.domain.usecase.SendMessageUseCase
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val speechRepository: SpeechRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ConversationUiState>(UiState.Loading)
    val uiState: StateFlow<ConversationUiState> = _uiState.asStateFlow()

    init {
        // Phase 1: no persisted history yet, start from an empty conversation.
        _uiState.value = UiState.Success(
            ConversationData(
                messages = emptyList(),
                playingMessageId = null,
                rate = 1f,
                isRepeatEnabled = false,
                isAutoPlayEnabled = false,
            ),
        )
        observeSpeechEvents()
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMessage = ChatMessage(
            id = System.currentTimeMillis(),
            sender = MessageSender.USER,
            text = text,
            timestamp = System.currentTimeMillis(),
        )
        updateData { it.copy(messages = it.messages + userMessage) }

        viewModelScope.launch {
            val result = sendMessageUseCase(text)
            result.onSuccess { aiMessage ->
                updateData { it.copy(messages = it.messages + aiMessage) }
                if (currentData()?.isAutoPlayEnabled == true) {
                    listenToMessage(aiMessage.id)
                }
            }.onFailure { error ->
                _uiState.value = UiState.Error(error.message ?: "Failed to reach the AI conversation partner")
            }
        }
    }

    fun listenToMessage(messageId: Long) {
        val data = currentData() ?: return
        val message = data.messages.firstOrNull { it.id == messageId } ?: return
        updateData { it.copy(playingMessageId = messageId) }
        speechRepository.speak(text = message.text, utteranceId = messageId.toString(), rate = data.rate)
    }

    fun stopListening() {
        speechRepository.stop()
        updateData { it.copy(playingMessageId = null) }
    }

    fun setPlaybackRate(rate: Float) {
        updateData { it.copy(rate = rate) }
    }

    fun toggleRepeat() {
        updateData { it.copy(isRepeatEnabled = !it.isRepeatEnabled) }
    }

    fun toggleAutoPlay() {
        updateData { it.copy(isAutoPlayEnabled = !it.isAutoPlayEnabled) }
    }

    private fun observeSpeechEvents() {
        viewModelScope.launch {
            speechRepository.ttsEvents().collect { event ->
                val data = currentData() ?: return@collect
                val playingId = data.playingMessageId ?: return@collect
                if (event !is SpeechEvent.Completed && event !is SpeechEvent.Failed) return@collect
                if (event.utteranceId != playingId.toString()) return@collect

                when {
                    event is SpeechEvent.Completed && data.isRepeatEnabled -> listenToMessage(playingId)
                    else -> updateData { it.copy(playingMessageId = null) }
                }
            }
        }
    }

    private fun currentData(): ConversationData? = (_uiState.value as? UiState.Success)?.data

    private fun updateData(transform: (ConversationData) -> ConversationData) {
        _uiState.update { state -> (state as? UiState.Success)?.let { UiState.Success(transform(it.data)) } ?: state }
    }
}
