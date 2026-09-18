package com.speakeng.app.feature.conversation.domain.usecase

import com.speakeng.app.feature.conversation.domain.model.MessageSender
import com.speakeng.app.feature.conversation.domain.repository.ConversationAiRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SendMessageUseCaseTest {

    private class FakeConversationAiRepository(private val result: Result<String>) : ConversationAiRepository {
        override suspend fun sendMessage(text: String): Result<String> = result
    }

    @Test
    fun `wraps a successful reply into an AI ChatMessage`() = runTest {
        val useCase = SendMessageUseCase(FakeConversationAiRepository(Result.success("Nice to meet you!")))

        val result = useCase("Hello")

        assertTrue(result.isSuccess)
        val message = result.getOrThrow()
        assertEquals("Nice to meet you!", message.text)
        assertEquals(MessageSender.AI, message.sender)
    }

    @Test
    fun `propagates a repository failure`() = runTest {
        val failure = IllegalStateException("boom")
        val useCase = SendMessageUseCase(FakeConversationAiRepository(Result.failure(failure)))

        val result = useCase("Hello")

        assertTrue(result.isFailure)
        assertEquals(failure, result.exceptionOrNull())
    }
}
