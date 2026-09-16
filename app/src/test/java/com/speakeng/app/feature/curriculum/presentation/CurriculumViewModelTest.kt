package com.speakeng.app.feature.curriculum.presentation

import app.cash.turbine.test
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.testing.MainDispatcherExtension
import com.speakeng.app.feature.curriculum.domain.model.CurriculumItem
import com.speakeng.app.feature.curriculum.domain.repository.CurriculumRepository
import com.speakeng.app.feature.curriculum.domain.usecase.GetCurriculumUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class CurriculumViewModelTest {

    private class FakeCurriculumRepository : CurriculumRepository {
        override suspend fun getCurriculum(): Result<List<CurriculumItem>> =
            Result.success(listOf(CurriculumItem(1, "Greetings", "Beginner", false)))
    }

    @Test
    fun `initial state is Loading`() = runTest {
        val viewModel = CurriculumViewModel(GetCurriculumUseCase(FakeCurriculumRepository()))

        viewModel.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
        }
    }
}
