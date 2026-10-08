package com.speakeng.app.feature.grammar.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.grammar.domain.usecase.GetGrammarLessonsUseCase
import com.speakeng.app.feature.grammar.domain.usecase.SetGrammarLessonCompletedUseCase
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class GrammarLessonDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getGrammarLessonsUseCase: GetGrammarLessonsUseCase,
    private val setGrammarLessonCompletedUseCase: SetGrammarLessonCompletedUseCase,
    private val speechRepository: SpeechRepository,
) : ViewModel() {

    private val lessonId: Int = checkNotNull(savedStateHandle.get<Int>(LESSON_ID_ARG)) { "Missing lessonId argument" }

    private val _uiState = MutableStateFlow<GrammarLessonDetailUiState>(UiState.Loading)
    val uiState: StateFlow<GrammarLessonDetailUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
        observeSpeechEvents()
    }

    fun play(exampleIndex: Int) {
        val data = currentData() ?: return
        val example = data.lesson.examples.getOrNull(exampleIndex) ?: return
        updateData { it.copy(playingExampleIndex = exampleIndex) }
        speechRepository.speak(text = example.english, utteranceId = exampleIndex.toString(), rate = 1f)
    }

    fun stop() {
        speechRepository.stop()
        updateData { it.copy(playingExampleIndex = null) }
    }

    fun toggleCompleted() {
        val data = currentData() ?: return
        val newCompleted = !data.isCompleted
        updateData { it.copy(isCompleted = newCompleted) }
        viewModelScope.launch {
            setGrammarLessonCompletedUseCase(lessonId, newCompleted)
        }
    }

    private fun loadLesson() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = getGrammarLessonsUseCase()
            _uiState.value = result.fold(
                onSuccess = { states ->
                    val state = states.firstOrNull { it.lesson.id == lessonId }
                    if (state == null) {
                        UiState.Error("Lesson not found")
                    } else {
                        UiState.Success(
                            GrammarLessonDetailData(
                                lesson = state.lesson,
                                isCompleted = state.isCompleted,
                                playingExampleIndex = null,
                            ),
                        )
                    }
                },
                onFailure = { UiState.Error(it.message ?: "Failed to load the lesson") },
            )
        }
    }

    private fun observeSpeechEvents() {
        viewModelScope.launch {
            speechRepository.ttsEvents().collect { event ->
                val data = currentData() ?: return@collect
                val playingIndex = data.playingExampleIndex ?: return@collect
                if (event !is SpeechEvent.Completed && event !is SpeechEvent.Failed) return@collect
                if (event.utteranceId != playingIndex.toString()) return@collect
                updateData { it.copy(playingExampleIndex = null) }
            }
        }
    }

    private fun currentData(): GrammarLessonDetailData? = (_uiState.value as? UiState.Success)?.data

    private fun updateData(transform: (GrammarLessonDetailData) -> GrammarLessonDetailData) {
        _uiState.update { state -> (state as? UiState.Success)?.let { UiState.Success(transform(it.data)) } ?: state }
    }

    private companion object {
        const val LESSON_ID_ARG = "lessonId"
    }
}
