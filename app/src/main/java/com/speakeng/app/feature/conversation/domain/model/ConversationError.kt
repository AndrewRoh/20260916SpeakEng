package com.speakeng.app.feature.conversation.domain.model

sealed class ConversationError(message: String) : Exception(message) {
    class NetworkError(message: String) : ConversationError(message)
    class AuthError(message: String) : ConversationError(message)
    class RateLimitError(message: String) : ConversationError(message)
    class UnknownError(message: String) : ConversationError(message)
}
