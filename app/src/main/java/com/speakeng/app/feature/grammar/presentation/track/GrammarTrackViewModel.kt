package com.speakeng.app.feature.grammar.presentation.track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.grammar.domain.usecase.GetGrammarLessonsUseCase
import com.speakeng.app.feature.grammar.domain.usecase.SetGrammarLessonCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class GrammarTrackViewModel @Inject constructor(
    private val getGrammarLessonsUseCase: GetGrammarLessonsUseCase,
    private val setGrammarLessonCompletedUseCase: SetGrammarLessonCompletedUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GrammarTrackUiState>(UiState.Loading)
    val uiState: StateFlow<GrammarTrackUiState> = _uiState.asStateFlow()

    init {
        loadLessons()
    }

    fun loadLessons() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = getGrammarLessonsUseCase()
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load grammar lessons") },
            )
        }
    }

    fun toggleCompleted(lessonId: Int) {
        val lessons = (_uiState.value as? UiState.Success)?.data ?: return
        val state = lessons.firstOrNull { it.lesson.id == lessonId } ?: return
        if (!state.isUnlocked) return
        val newCompleted = !state.isCompleted

        _uiState.value = UiState.Success(
            lessons.map { if (it.lesson.id == lessonId) it.copy(isCompleted = newCompleted) else it },
        )
        viewModelScope.launch {
            setGrammarLessonCompletedUseCase(lessonId, newCompleted)
        }
    }
}
