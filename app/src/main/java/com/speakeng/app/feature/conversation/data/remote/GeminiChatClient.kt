package com.speakeng.app.feature.conversation.data.remote

/** Thin seam over the Gemini SDK's chat session so ConversationAiRepositoryImpl is unit-testable. */
interface GeminiChatClient {
    suspend fun sendMessage(text: String): String
}
