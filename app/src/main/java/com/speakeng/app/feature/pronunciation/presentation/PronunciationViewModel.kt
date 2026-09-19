package com.speakeng.app.feature.pronunciation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.model.SpeechRecognitionState
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import com.speakeng.app.feature.pronunciation.domain.usecase.EvaluatePronunciationUseCase
import com.speakeng.app.feature.reading.domain.model.Book
import com.speakeng.app.feature.reading.domain.usecase.GetBooksUseCase
import com.speakeng.app.feature.reading.domain.usecase.LoadReadingSentencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PronunciationViewModel @Inject constructor(
    private val getBooksUseCase: GetBooksUseCase,
    private val loadReadingSentencesUseCase: LoadReadingSentencesUseCase,
    private val evaluatePronunciationUseCase: EvaluatePronunciationUseCase,
    private val speechRepository: SpeechRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<PronunciationUiState>(UiState.Loading)
    val uiState: StateFlow<PronunciationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getBooksUseCase().collect { books -> onBooksLoaded(books) }
        }
        observeTtsEvents()
    }

    fun selectBook(bookId: String) {
        val data = currentData() ?: return
        val book = data.books.firstOrNull { it.id == bookId } ?: return
        updateData { it.copy(selectedBook = book, sentences = emptyList(), selectedSentenceIndex = 0, result = null) }

        viewModelScope.launch {
            loadReadingSentencesUseCase(bookId)
                .onSuccess { sentences -> updateData { it.copy(sentences = sentences) } }
                .onFailure { error -> _uiState.value = UiState.Error(error.message ?: "Failed to load the book") }
        }
    }

    fun selectSentence(index: Int) {
        val data = currentData() ?: return
        if (index !in data.sentences.indices) return
        updateData { it.copy(selectedSentenceIndex = index, result = null) }
    }

    fun listenToSentence() {
        val data = currentData() ?: return
        val sentence = data.sentences.getOrNull(data.selectedSentenceIndex) ?: return
        updateData { it.copy(isPlayingTts = true) }
        speechRepository.speak(text = sentence.text, utteranceId = TTS_UTTERANCE_ID, rate = data.ttsRate)
    }

    fun stopTts() {
        speechRepository.stop()
        updateData { it.copy(isPlayingTts = false) }
    }

    fun setTtsRate(rate: Float) {
        updateData { it.copy(ttsRate = rate) }
    }

    fun toggleTtsRepeat() {
        updateData { it.copy(isTtsRepeatEnabled = !it.isTtsRepeatEnabled) }
    }

    fun onMicPermissionResult(granted: Boolean) {
        updateData { it.copy(hasRecordPermission = granted) }
    }

    fun startListening() {
        val data = currentData() ?: return
        if (!data.hasRecordPermission) return
        val sentence = data.sentences.getOrNull(data.selectedSentenceIndex) ?: return

        updateData { it.copy(isListening = true, result = null, micErrorMessage = null) }
        viewModelScope.launch {
            speechRepository.startListening().collect { state ->
                when (state) {
                    is SpeechRecognitionState.Idle -> Unit
                    is SpeechRecognitionState.Listening -> updateData { it.copy(isListening = true) }
                    is SpeechRecognitionState.Result -> onRecognitionResult(sentence.text, state.text)
                    is SpeechRecognitionState.Error -> {
                        updateData { it.copy(isListening = false, micErrorMessage = state.message) }
                    }
                }
            }
        }
    }

    fun dismissMicError() {
        updateData { it.copy(micErrorMessage = null) }
    }

    fun stopListening() {
        speechRepository.stopListening()
        updateData { it.copy(isListening = false) }
    }

    private fun onRecognitionResult(targetText: String, recognizedText: String) {
        updateData { it.copy(isListening = false) }
        evaluatePronunciationUseCase(targetText, recognizedText)
            .onSuccess { result ->
                updateData { it.copy(result = result, sessionScores = it.sessionScores + result.accuracy) }
            }
            .onFailure { error ->
                updateData { it.copy(micErrorMessage = error.message ?: "Failed to evaluate pronunciation") }
            }
    }

    private fun observeTtsEvents() {
        viewModelScope.launch {
            speechRepository.ttsEvents().collect { event ->
                if (event.utteranceId != TTS_UTTERANCE_ID) return@collect
                val data = currentData() ?: return@collect
                when (event) {
                    is SpeechEvent.Completed -> {
                        if (data.isTtsRepeatEnabled) listenToSentence() else updateData { it.copy(isPlayingTts = false) }
                    }
                    is SpeechEvent.Failed -> updateData { it.copy(isPlayingTts = false) }
                    is SpeechEvent.Started -> Unit
                }
            }
        }
    }

    private fun onBooksLoaded(books: List<Book>) {
        _uiState.update { state ->
            val current = (state as? UiState.Success)?.data
            UiState.Success(
                current?.copy(books = books) ?: PronunciationData(
                    books = books,
                    selectedBook = null,
                    sentences = emptyList(),
                    selectedSentenceIndex = 0,
                    isListening = false,
                    isPlayingTts = false,
                    ttsRate = 1f,
                    isTtsRepeatEnabled = false,
                    result = null,
                    hasRecordPermission = false,
                    sessionScores = emptyList(),
                    micErrorMessage = null,
                ),
            )
        }
    }

    private fun currentData(): PronunciationData? = (_uiState.value as? UiState.Success)?.data

    private fun updateData(transform: (PronunciationData) -> PronunciationData) {
        _uiState.update { state -> (state as? UiState.Success)?.let { UiState.Success(transform(it.data)) } ?: state }
    }

    private companion object {
        const val TTS_UTTERANCE_ID = "pronunciation-tts"
    }
}
