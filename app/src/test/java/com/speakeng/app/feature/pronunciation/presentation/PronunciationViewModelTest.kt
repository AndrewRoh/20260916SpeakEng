package com.speakeng.app.feature.pronunciation.presentation

import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.testing.MainDispatcherExtension
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import com.speakeng.app.feature.pronunciation.domain.usecase.AnalyzePronunciationUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class PronunciationViewModelTest {

    private class FakeSpeechRepository : SpeechRepository {
        override fun startListening(): Flow<String> = flowOf("hello")
        override fun stopListening() = Unit
        override fun speak(text: String, utteranceId: String, rate: Float) = Unit
        override fun stop() = Unit
        override fun ttsEvents(): Flow<SpeechEvent> = emptyFlow()
    }

    @Test
    fun `initial state has no result yet`() = runTest {
        val viewModel = PronunciationViewModel(AnalyzePronunciationUseCase(FakeSpeechRepository()))

        viewModel.uiState.test {
            assertEquals(UiState.Success(null), awaitItem())
        }
    }
}
