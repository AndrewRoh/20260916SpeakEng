package com.speakeng.app.feature.conversation.presentation

import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.testing.MainDispatcherExtension
import com.speakeng.app.feature.conversation.domain.model.MessageSender
import com.speakeng.app.feature.conversation.domain.repository.ConversationAiRepository
import com.speakeng.app.feature.conversation.domain.usecase.SendMessageUseCase
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.model.SpeechRecognitionState
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class ConversationViewModelTest {

    private class FakeConversationAiRepository : ConversationAiRepository {
        override suspend fun sendMessage(text: String): Result<String> = Result.success("stub reply")
    }

    private class FakeSpeechRepository : SpeechRepository {
        var lastSpoken: String? = null
        override fun startListening(): Flow<SpeechRecognitionState> = emptyFlow()
        override fun stopListening() = Unit
        override fun speak(text: String, utteranceId: String, rate: Float) {
            lastSpoken = text
        }
        override fun stop() = Unit
        override fun ttsEvents(): Flow<SpeechEvent> = emptyFlow()
    }

    private fun newViewModel(speechRepository: SpeechRepository = FakeSpeechRepository()) =
        ConversationViewModel(SendMessageUseCase(FakeConversationAiRepository()), speechRepository)

    private fun ConversationViewModel.data() = (uiState.value as UiState.Success).data

    @Test
    fun `initial state is an empty conversation`() = runTest {
        val viewModel = newViewModel()

        viewModel.uiState.test {
            assertEquals(
                UiState.Success(ConversationData(emptyList(), null, 1f, false, false)),
                awaitItem(),
            )
        }
    }

    @Test
    fun `sendMessage immediately appends the user's message`() = runTest {
        val viewModel = newViewModel()

        viewModel.sendMessage("Hello")

        val data = viewModel.data()
        assertEquals(1, data.messages.size)
        assertEquals(MessageSender.USER, data.messages.first().sender)
        assertEquals("Hello", data.messages.first().text)
    }

    @Test
    fun `listenToMessage marks the message as playing and speaks it`() = runTest {
        val speechRepository = FakeSpeechRepository()
        val viewModel = newViewModel(speechRepository)
        viewModel.sendMessage("Hello")
        val messageId = viewModel.data().messages.first().id

        viewModel.listenToMessage(messageId)

        val data = viewModel.data()
        assertEquals(messageId, data.playingMessageId)
        assertEquals("Hello", speechRepository.lastSpoken)
    }

    @Test
    fun `stopListening clears the playing message`() = runTest {
        val viewModel = newViewModel()
        viewModel.sendMessage("Hello")
        val messageId = viewModel.data().messages.first().id
        viewModel.listenToMessage(messageId)

        viewModel.stopListening()

        assertNull(viewModel.data().playingMessageId)
    }

    @Test
    fun `setPlaybackRate updates the rate`() = runTest {
        val viewModel = newViewModel()

        viewModel.setPlaybackRate(0.5f)

        assertEquals(0.5f, viewModel.data().rate)
    }

    @Test
    fun `toggleRepeat flips the repeat flag`() = runTest {
        val viewModel = newViewModel()

        viewModel.toggleRepeat()

        assertEquals(true, viewModel.data().isRepeatEnabled)
    }

    @Test
    fun `toggleAutoPlay flips the auto-play flag`() = runTest {
        val viewModel = newViewModel()

        viewModel.toggleAutoPlay()

        assertEquals(true, viewModel.data().isAutoPlayEnabled)
    }
}
