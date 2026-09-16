package com.speakeng.app.feature.conversation.domain.repository

/**
 * Sends learner input to the conversational AI backend and returns its reply.
 * TODO(Phase 2): implement against the Claude API.
 */
interface ConversationAiRepository {
    suspend fun sendMessage(text: String): Result<String>
}
