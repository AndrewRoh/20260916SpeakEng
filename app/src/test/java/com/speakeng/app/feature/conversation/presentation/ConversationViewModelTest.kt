package com.speakeng.app.feature.conversation.presentation

import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.testing.MainDispatcherExtension
import com.speakeng.app.feature.conversation.domain.repository.ConversationAiRepository
import com.speakeng.app.feature.conversation.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class ConversationViewModelTest {

    private class FakeConversationAiRepository : ConversationAiRepository {
        override suspend fun sendMessage(text: String): Result<String> = Result.success("stub reply")
    }

    @Test
    fun `initial state is an empty conversation`() = runTest {
        val viewModel = ConversationViewModel(SendMessageUseCase(FakeConversationAiRepository()))

        viewModel.uiState.test {
            assertEquals(UiState.Success(emptyList()), awaitItem())
        }
    }
}
