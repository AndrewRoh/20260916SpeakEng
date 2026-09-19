package com.speakeng.app.feature.conversation.domain.repository

import com.speakeng.app.feature.conversation.domain.model.ChatMessage

/** Persists conversation turns locally so history survives app restarts. */
interface ConversationHistoryRepository {
    suspend fun getMessages(): Result<List<ChatMessage>>
    suspend fun saveMessage(message: ChatMessage): Result<Unit>
}
