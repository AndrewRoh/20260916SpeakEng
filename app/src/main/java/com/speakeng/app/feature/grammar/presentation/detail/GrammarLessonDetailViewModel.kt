package com.speakeng.app.feature.grammar.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.core.di.ApplicationScope
import com.speakeng.app.feature.grammar.domain.model.GrammarExample
import com.speakeng.app.feature.grammar.domain.usecase.GetGrammarLessonsUseCase
import com.speakeng.app.feature.grammar.domain.usecase.SetGrammarLessonCompletedUseCase
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
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
    @ApplicationScope private val applicationScope: CoroutineScope,
) : ViewModel() {

    private val lessonId: Int = checkNotNull(savedStateHandle.get<Int>(LESSON_ID_ARG)) { "Missing lessonId argument" }

    private val _uiState = MutableStateFlow<GrammarLessonDetailUiState>(UiState.Loading)
    val uiState: StateFlow<GrammarLessonDetailUiState> = _uiState.asStateFlow()

    // Sequence state for play-all/random runs. Not part of UiState since the UI only needs to
    // know a sequence is "playing" (isSequencePlaying), not the queue itself.
    private var playQueue: List<Int>? = null
    private var playQueuePosition: Int = 0

    // Bumped on every stop()/play()/playAll() so a stale delayed "advance to next sentence"
    // coroutine (scheduled during the listen-and-repeat pause) can detect it was superseded.
    private var playbackGeneration: Int = 0

    init {
        loadLesson()
        observeSpeechEvents()
    }

    fun play(exampleIndex: Int) {
        playQueue = null
        playbackGeneration++
        updateData { it.copy(isSequencePlaying = false, isAwaitingRepeat = false) }
        startPlayback(exampleIndex)
    }

    fun playAll(shuffled: Boolean) {
        val data = currentData() ?: return
        val indices = data.lesson.examples.indices.toList()
        if (indices.isEmpty()) return
        val queue = if (shuffled) indices.shuffled() else indices
        playQueue = queue
        playQueuePosition = 0
        playbackGeneration++
        updateData { it.copy(isSequencePlaying = true, isSingleRepeatEnabled = false, isAwaitingRepeat = false) }
        startPlayback(queue.first())
    }

    fun stop() {
        playQueue = null
        playbackGeneration++
        speechRepository.stop()
        updateData { it.copy(playingExampleIndex = null, isSequencePlaying = false, isAwaitingRepeat = false) }
    }

    fun setRate(rate: Float) {
        updateData { it.copy(rate = rate) }
    }

    fun toggleListenAndRepeat() {
        updateData { it.copy(isListenAndRepeatEnabled = !it.isListenAndRepeatEnabled) }
    }

    fun toggleSingleRepeat() {
        if (playQueue != null) stop()
        updateData { it.copy(isSingleRepeatEnabled = !it.isSingleRepeatEnabled) }
    }

    fun toggleCompleted() {
        val data = currentData() ?: return
        val newCompleted = !data.isCompleted
        updateData { it.copy(isCompleted = newCompleted) }
        // Persisted on applicationScope, not viewModelScope: this must survive navigating away
        // from the screen right after tapping, which cancels viewModelScope before the write lands.
        applicationScope.launch {
            setGrammarLessonCompletedUseCase(lessonId, newCompleted)
        }
    }

    private fun startPlayback(exampleIndex: Int) {
        val data = currentData() ?: return
        val example = data.lesson.examples.getOrNull(exampleIndex) ?: return
        updateData { it.copy(playingExampleIndex = exampleIndex, isAwaitingRepeat = false) }
        speechRepository.speak(text = example.english, utteranceId = exampleIndex.toString(), rate = data.rate)
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
                onPlaybackFinished(data, playingIndex)
            }
        }
    }

    private fun onPlaybackFinished(data: GrammarLessonDetailData, finishedIndex: Int) {
        val queue = playQueue
        when {
            data.isSingleRepeatEnabled && queue == null -> startPlayback(finishedIndex)
            queue != null -> advanceQueue(data, finishedIndex, queue)
            else -> updateData { it.copy(playingExampleIndex = null) }
        }
    }

    private fun advanceQueue(data: GrammarLessonDetailData, finishedIndex: Int, queue: List<Int>) {
        val nextPosition = playQueuePosition + 1
        if (nextPosition >= queue.size) {
            playQueue = null
            updateData { it.copy(playingExampleIndex = null, isSequencePlaying = false, isAwaitingRepeat = false) }
            return
        }
        playQueuePosition = nextPosition
        val nextIndex = queue[nextPosition]
        if (!data.isListenAndRepeatEnabled) {
            startPlayback(nextIndex)
            return
        }
        val pauseMillis = repeatPauseMillis(data.lesson.examples.getOrNull(finishedIndex))
        updateData { it.copy(isAwaitingRepeat = true) }
        val generationAtPause = playbackGeneration
        viewModelScope.launch {
            delay(pauseMillis)
            if (playbackGeneration == generationAtPause) {
                startPlayback(nextIndex)
            }
        }
    }

    /** Word-count-scaled pause so the learner has time to repeat the sentence aloud (2-6s). */
    private fun repeatPauseMillis(example: GrammarExample?): Long {
        val wordCount = example?.english?.split(" ")?.size ?: 1
        return (wordCount * 600L).coerceIn(2000L, 6000L)
    }

    private fun currentData(): GrammarLessonDetailData? = (_uiState.value as? UiState.Success)?.data

    private fun updateData(transform: (GrammarLessonDetailData) -> GrammarLessonDetailData) {
        _uiState.update { state -> (state as? UiState.Success)?.let { UiState.Success(transform(it.data)) } ?: state }
    }

    private companion object {
        const val LESSON_ID_ARG = "lessonId"
    }
}
