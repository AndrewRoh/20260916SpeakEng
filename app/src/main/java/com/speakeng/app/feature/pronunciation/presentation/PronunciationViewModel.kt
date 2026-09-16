package com.speakeng.app.feature.pronunciation.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.pronunciation.domain.usecase.AnalyzePronunciationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PronunciationViewModel @Inject constructor(
    private val analyzePronunciationUseCase: AnalyzePronunciationUseCase,
) : ViewModel() {

    // No result yet until the learner records something.
    private val _uiState = MutableStateFlow<PronunciationUiState>(UiState.Success(null))
    val uiState: StateFlow<PronunciationUiState> = _uiState.asStateFlow()

    fun onRecordClick() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = analyzePronunciationUseCase()
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to analyze pronunciation") },
            )
        }
    }
}
