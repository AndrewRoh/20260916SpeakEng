package com.speakeng.app.feature.conversation.data.repository

import android.util.Log
import com.speakeng.app.feature.conversation.data.remote.GeminiChatClient
import com.speakeng.app.feature.conversation.domain.model.ConversationError
import com.speakeng.app.feature.conversation.domain.repository.ConversationAiRepository
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class ConversationAiRepositoryImpl @Inject constructor(
    private val geminiChatClient: GeminiChatClient,
) : ConversationAiRepository {

    override suspend fun sendMessage(text: String): Result<String> = try {
        Result.success(geminiChatClient.sendMessage(text))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(mapToConversationError(e))
    }

    /**
     * Matches on the exception's message rather than importing specific `generativeai` SDK
     * exception subclasses, since those types have shifted across SDK versions; the message
     * text (surfaced from the underlying HTTP/gRPC error) is a more stable signal. The raw
     * message is logged for debugging but never shown to the user — it can be a large JSON/SDK
     * dump (e.g. a bare 403 response body) that isn't fit for display; see [ConversationError].
     */
    private fun mapToConversationError(e: Exception): ConversationError {
        val message = e.message ?: e.toString()
        Log.w(TAG, "Gemini request failed: $message", e)
        return when {
            message.contains("API key", ignoreCase = true) ||
                message.contains("PERMISSION_DENIED", ignoreCase = true) ->
                ConversationError.AuthError(e)

            message.contains("429") ||
                message.contains("quota", ignoreCase = true) ||
                message.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
                message.contains("rate limit", ignoreCase = true) ->
                ConversationError.RateLimitError(e)

            e is IOException -> ConversationError.NetworkError(e)

            else -> ConversationError.UnknownError(e)
        }
    }

    private companion object {
        const val TAG = "ConversationAiRepository"
    }
}
