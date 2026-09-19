package com.speakeng.app.feature.conversation.presentation

import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.model.MessageSender
import com.speakeng.app.feature.conversation.domain.repository.ConversationAiRepository
import com.speakeng.app.feature.conversation.domain.repository.ConversationHistoryRepository
import com.speakeng.app.feature.conversation.domain.usecase.LoadConversationHistoryUseCase
import com.speakeng.app.feature.conversation.domain.usecase.SaveConversationMessageUseCase
import com.speakeng.app.feature.conversation.domain.usecase.SendMessageUseCase
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.model.SpeechRecognitionState
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConversationViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeConversationAiRepository : ConversationAiRepository {
        override suspend fun sendMessage(text: String): Result<String> = Result.success("stub reply")
    }

    private class FakeConversationHistoryRepository(
        seed: List<ChatMessage> = emptyList(),
    ) : ConversationHistoryRepository {
        val savedMessages = mutableListOf<ChatMessage>().apply { addAll(seed) }

        override suspend fun getMessages(): Result<List<ChatMessage>> = Result.success(savedMessages.toList())

        override suspend fun saveMessage(message: ChatMessage): Result<Unit> {
            savedMessages.add(message)
            return Result.success(Unit)
        }
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

    private fun newViewModel(
        historyRepository: FakeConversationHistoryRepository = FakeConversationHistoryRepository(),
        speechRepository: SpeechRepository = FakeSpeechRepository(),
    ) = ConversationViewModel(
        SendMessageUseCase(FakeConversationAiRepository()),
        LoadConversationHistoryUseCase(historyRepository),
        SaveConversationMessageUseCase(historyRepository),
        speechRepository,
    )

    private fun ConversationViewModel.data() = (uiState.value as UiState.Success).data

    @Test
    fun `initial state is an empty conversation when there is no saved history`() = runTest(testDispatcher) {
        val viewModel = newViewModel()

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(
                UiState.Success(ConversationData(emptyList(), null, 1f, false, false, null)),
                awaitItem(),
            )
        }
    }

    @Test
    fun `initial state restores previously saved history`() = runTest(testDispatcher) {
        val seeded = listOf(ChatMessage(1L, MessageSender.AI, "Welcome back!", 0L))
        val viewModel = newViewModel(historyRepository = FakeConversationHistoryRepository(seeded))
        advanceUntilIdle()

        assertEquals(seeded, viewModel.data().messages)
    }

    @Test
    fun `sendMessage immediately appends the user's message`() = runTest(testDispatcher) {
        val viewModel = newViewModel()
        advanceUntilIdle()

        viewModel.sendMessage("Hello")

        val data = viewModel.data()
        assertEquals(1, data.messages.size)
        assertEquals(MessageSender.USER, data.messages.first().sender)
        assertEquals("Hello", data.messages.first().text)
    }

    @Test
    fun `sendMessage persists both the user message and the AI reply`() = runTest(testDispatcher) {
        val historyRepository = FakeConversationHistoryRepository()
        val viewModel = newViewModel(historyRepository = historyRepository)
        advanceUntilIdle()

        viewModel.sendMessage("Hello")
        advanceUntilIdle()

        assertEquals(2, historyRepository.savedMessages.size)
        assertEquals(MessageSender.USER, historyRepository.savedMessages[0].sender)
        assertEquals(MessageSender.AI, historyRepository.savedMessages[1].sender)
    }

    @Test
    fun `listenToMessage marks the message as playing and speaks it`() = runTest(testDispatcher) {
        val speechRepository = FakeSpeechRepository()
        val viewModel = newViewModel(speechRepository = speechRepository)
        advanceUntilIdle()
        viewModel.sendMessage("Hello")
        val messageId = viewModel.data().messages.first().id

        viewModel.listenToMessage(messageId)

        val data = viewModel.data()
        assertEquals(messageId, data.playingMessageId)
        assertEquals("Hello", speechRepository.lastSpoken)
    }

    @Test
    fun `stopListening clears the playing message`() = runTest(testDispatcher) {
        val viewModel = newViewModel()
        advanceUntilIdle()
        viewModel.sendMessage("Hello")
        val messageId = viewModel.data().messages.first().id
        viewModel.listenToMessage(messageId)

        viewModel.stopListening()

        assertNull(viewModel.data().playingMessageId)
    }

    @Test
    fun `setPlaybackRate updates the rate`() = runTest(testDispatcher) {
        val viewModel = newViewModel()
        advanceUntilIdle()

        viewModel.setPlaybackRate(0.5f)

        assertEquals(0.5f, viewModel.data().rate)
    }

    @Test
    fun `toggleRepeat flips the repeat flag`() = runTest(testDispatcher) {
        val viewModel = newViewModel()
        advanceUntilIdle()

        viewModel.toggleRepeat()

        assertEquals(true, viewModel.data().isRepeatEnabled)
    }

    @Test
    fun `toggleAutoPlay flips the auto-play flag`() = runTest(testDispatcher) {
        val viewModel = newViewModel()
        advanceUntilIdle()

        viewModel.toggleAutoPlay()

        assertEquals(true, viewModel.data().isAutoPlayEnabled)
    }
}
