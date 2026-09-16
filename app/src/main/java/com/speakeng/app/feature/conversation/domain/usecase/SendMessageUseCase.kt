package com.speakeng.app.feature.conversation.domain.usecase

import com.speakeng.app.feature.conversation.domain.repository.ConversationAiRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val conversationAiRepository: ConversationAiRepository,
) {
    suspend operator fun invoke(text: String): Result<String> =
        conversationAiRepository.sendMessage(text)
}
