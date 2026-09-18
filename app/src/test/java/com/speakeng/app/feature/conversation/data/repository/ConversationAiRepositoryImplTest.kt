package com.speakeng.app.feature.conversation.data.repository

import com.speakeng.app.feature.conversation.data.remote.GeminiChatClient
import com.speakeng.app.feature.conversation.domain.model.ConversationError
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ConversationAiRepositoryImplTest {

    private class FakeGeminiChatClient(
        private val result: suspend () -> String,
    ) : GeminiChatClient {
        override suspend fun sendMessage(text: String): String = result()
    }

    @Test
    fun `sendMessage returns the reply on success`() = runTest {
        val repository = ConversationAiRepositoryImpl(FakeGeminiChatClient { "Hello there!" })

        val result = repository.sendMessage("Hi")

        assertTrue(result.isSuccess)
        assertEquals("Hello there!", result.getOrThrow())
    }

    @Test
    fun `sendMessage maps an API key failure to AuthError`() = runTest {
        val repository = ConversationAiRepositoryImpl(
            FakeGeminiChatClient { throw IllegalStateException("API key not valid") },
        )

        val result = repository.sendMessage("Hi")

        assertTrue(result.isFailure)
        assertInstanceOf(ConversationError.AuthError::class.java, result.exceptionOrNull())
    }

    @Test
    fun `sendMessage maps a quota failure to RateLimitError`() = runTest {
        val repository = ConversationAiRepositoryImpl(
            FakeGeminiChatClient { throw IllegalStateException("429 RESOURCE_EXHAUSTED: quota exceeded") },
        )

        val result = repository.sendMessage("Hi")

        assertTrue(result.isFailure)
        assertInstanceOf(ConversationError.RateLimitError::class.java, result.exceptionOrNull())
    }

    @Test
    fun `sendMessage maps an IOException to NetworkError`() = runTest {
        val repository = ConversationAiRepositoryImpl(
            FakeGeminiChatClient { throw IOException("Unable to resolve host") },
        )

        val result = repository.sendMessage("Hi")

        assertTrue(result.isFailure)
        assertInstanceOf(ConversationError.NetworkError::class.java, result.exceptionOrNull())
    }

    @Test
    fun `sendMessage maps an unrecognized failure to UnknownError`() = runTest {
        val repository = ConversationAiRepositoryImpl(
            FakeGeminiChatClient { throw IllegalStateException("something odd happened") },
        )

        val result = repository.sendMessage("Hi")

        assertTrue(result.isFailure)
        assertInstanceOf(ConversationError.UnknownError::class.java, result.exceptionOrNull())
    }
}
