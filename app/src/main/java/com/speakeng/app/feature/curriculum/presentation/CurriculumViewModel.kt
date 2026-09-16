package com.speakeng.app.feature.curriculum.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.speakeng.app.core.common.UiState
import com.speakeng.app.feature.curriculum.domain.usecase.GetCurriculumUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurriculumViewModel @Inject constructor(
    private val getCurriculumUseCase: GetCurriculumUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CurriculumUiState>(UiState.Loading)
    val uiState: StateFlow<CurriculumUiState> = _uiState.asStateFlow()

    init {
        loadCurriculum()
    }

    fun loadCurriculum() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = getCurriculumUseCase()
            _uiState.value = result.fold(
                onSuccess = { UiState.Success(it) },
                onFailure = { UiState.Error(it.message ?: "Failed to load curriculum") },
            )
        }
    }
}
