package com.speakeng.app.feature.grammar.domain.usecase

import com.speakeng.app.feature.grammar.domain.repository.GrammarLessonRepository
import javax.inject.Inject

class SetGrammarLessonCompletedUseCase @Inject constructor(
    private val grammarLessonRepository: GrammarLessonRepository,
) {
    suspend operator fun invoke(lessonId: Int, isCompleted: Boolean): Result<Unit> =
        grammarLessonRepository.setCompleted(lessonId, isCompleted)
}
