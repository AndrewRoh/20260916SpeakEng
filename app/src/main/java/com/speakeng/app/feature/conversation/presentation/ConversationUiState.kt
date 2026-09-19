package com.speakeng.app.feature.conversation.presentation

import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.conversation.domain.model.ChatMessage

data class ConversationData(
    val messages: List<ChatMessage>,
    val playingMessageId: Long?,
    val rate: Float,
    val isRepeatEnabled: Boolean,
    val isAutoPlayEnabled: Boolean,
    /** Shown as a dismissible inline notice; a failed send shouldn't wipe the conversation. */
    val errorMessage: String?,
)

typealias ConversationUiState = UiState<ConversationData>
