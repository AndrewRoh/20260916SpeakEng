package com.speakeng.app.feature.conversation.domain.model

/**
 * User-facing conversation failures. Each subtype carries a short, friendly default message —
 * never the raw SDK/HTTP exception text, which can be a large technical dump unsuitable for
 * display (see ConversationAiRepositoryImpl.mapToConversationError).
 */
sealed class ConversationError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkError(cause: Throwable? = null) :
        ConversationError("Network error. Please check your connection and try again.", cause)

    class AuthError(cause: Throwable? = null) :
        ConversationError("AI conversation isn't set up yet (missing or invalid API key).", cause)

    class RateLimitError(cause: Throwable? = null) :
        ConversationError("Too many requests right now. Please wait a moment and try again.", cause)

    class UnknownError(cause: Throwable? = null) :
        ConversationError("Couldn't reach the AI. Please try again.", cause)
}
