package com.speakeng.app.feature.reading.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import com.speakeng.app.feature.reading.domain.usecase.LoadReadingSentencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.net.URLDecoder
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReadingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val loadReadingSentencesUseCase: LoadReadingSentencesUseCase,
    private val speechRepository: SpeechRepository,
) : ViewModel() {

    private val bookId: String = URLDecoder.decode(
        checkNotNull(savedStateHandle.get<String>(BOOK_ID_ARG)) { "Missing bookId argument" },
        Charsets.UTF_8.name(),
    )

    private val _uiState = MutableStateFlow<ReadingUiState>(UiState.Loading)
    val uiState: StateFlow<ReadingUiState> = _uiState.asStateFlow()

    init {
        loadSentences()
        observeSpeechEvents()
    }

    fun play() {
        currentData()?.let(::playCurrentSentence)
    }

    fun stop() {
        speechRepository.stop()
        updateData { it.copy(isPlaying = false) }
    }

    fun next() = moveTo { it.currentIndex + 1 }

    fun previous() = moveTo { it.currentIndex - 1 }

    fun setRate(rate: Float) {
        updateData { it.copy(rate = rate) }
    }

    fun toggleRepeat() {
        updateData { it.copy(isRepeatEnabled = !it.isRepeatEnabled) }
    }

    private fun loadSentences() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = loadReadingSentencesUseCase(bookId)
            _uiState.value = result.fold(
                onSuccess = { sentences ->
                    UiState.Success(
                        ReadingData(
                            sentences = sentences,
                            currentIndex = 0,
                            isPlaying = false,
                            rate = 1f,
                            isRepeatEnabled = false,
                        ),
                    )
                },
                onFailure = { UiState.Error(it.message ?: "Failed to load the book") },
            )
        }
    }

    private fun observeSpeechEvents() {
        viewModelScope.launch {
            speechRepository.ttsEvents().collect { event ->
                when (event) {
                    is SpeechEvent.Completed -> onSentenceFinished()
                    is SpeechEvent.Failed -> _uiState.value = UiState.Error("Text-to-speech playback failed")
                    is SpeechEvent.Started -> Unit
                }
            }
        }
    }

    private fun onSentenceFinished() {
        val data = currentData() ?: return
        when {
            data.isRepeatEnabled -> playCurrentSentence(data)
            data.currentIndex < data.sentences.lastIndex -> {
                updateData { it.copy(currentIndex = it.currentIndex + 1) }
                currentData()?.let(::playCurrentSentence)
            }
            else -> updateData { it.copy(isPlaying = false) }
        }
    }

    private fun moveTo(nextIndex: (ReadingData) -> Int) {
        val data = currentData() ?: return
        val target = nextIndex(data)
        if (target !in data.sentences.indices) return

        speechRepository.stop()
        val wasPlaying = data.isPlaying
        updateData { it.copy(currentIndex = target, isPlaying = false) }
        if (wasPlaying) currentData()?.let(::playCurrentSentence)
    }

    private fun playCurrentSentence(data: ReadingData) {
        val sentence = data.sentences.getOrNull(data.currentIndex) ?: return
        updateData { it.copy(isPlaying = true) }
        speechRepository.speak(text = sentence.text, utteranceId = sentence.index.toString(), rate = data.rate)
    }

    private fun currentData(): ReadingData? = (_uiState.value as? UiState.Success)?.data

    private fun updateData(transform: (ReadingData) -> ReadingData) {
        _uiState.update { state -> (state as? UiState.Success)?.let { UiState.Success(transform(it.data)) } ?: state }
    }

    private companion object {
        const val BOOK_ID_ARG = "bookId"
    }
}
