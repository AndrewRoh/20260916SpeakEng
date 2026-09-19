package com.speakeng.app.feature.conversation.domain.usecase

import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.repository.ConversationHistoryRepository
import javax.inject.Inject

/** One-shot load of persisted conversation history, used to restore it on app start. */
class LoadConversationHistoryUseCase @Inject constructor(
    private val conversationHistoryRepository: ConversationHistoryRepository,
) {
    suspend operator fun invoke(): Result<List<ChatMessage>> = conversationHistoryRepository.getMessages()
}
