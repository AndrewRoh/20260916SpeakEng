package com.speakeng.app.feature.conversation.domain.usecase

import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.model.MessageSender
import com.speakeng.app.feature.conversation.domain.repository.ConversationAiRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val conversationAiRepository: ConversationAiRepository,
) {
    suspend operator fun invoke(text: String): Result<ChatMessage> =
        conversationAiRepository.sendMessage(text).map { reply ->
            ChatMessage(
                id = System.currentTimeMillis(),
                sender = MessageSender.AI,
                text = reply,
                timestamp = System.currentTimeMillis(),
            )
        }
}
