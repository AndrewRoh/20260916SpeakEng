package com.speakeng.app.feature.grammar.domain.usecase

import com.speakeng.app.feature.grammar.domain.model.GrammarLessonState
import com.speakeng.app.feature.grammar.domain.repository.GrammarLessonRepository
import javax.inject.Inject

class GetGrammarLessonsUseCase @Inject constructor(
    private val grammarLessonRepository: GrammarLessonRepository,
) {
    suspend operator fun invoke(): Result<List<GrammarLessonState>> = grammarLessonRepository.getLessons()
}
