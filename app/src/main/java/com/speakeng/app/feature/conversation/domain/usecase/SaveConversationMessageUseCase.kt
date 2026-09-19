package com.speakeng.app.feature.conversation.domain.usecase

import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.repository.ConversationHistoryRepository
import javax.inject.Inject

class SaveConversationMessageUseCase @Inject constructor(
    private val conversationHistoryRepository: ConversationHistoryRepository,
) {
    suspend operator fun invoke(message: ChatMessage): Result<Unit> = conversationHistoryRepository.saveMessage(message)
}
