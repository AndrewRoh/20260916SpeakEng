package com.speakeng.app.feature.conversation.domain.repository

/**
 * Sends learner input to the conversational AI backend and returns its reply.
 * Implemented against the Gemini API (see ConversationAiRepositoryImpl); failures are
 * wrapped as [com.speakeng.app.feature.conversation.domain.model.ConversationError].
 */
interface ConversationAiRepository {
    suspend fun sendMessage(text: String): Result<String>
}
