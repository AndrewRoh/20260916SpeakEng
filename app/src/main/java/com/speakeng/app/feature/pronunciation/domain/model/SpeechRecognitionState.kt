package com.speakeng.app.feature.pronunciation.domain.model

/** States emitted while listening for speech, from tap to final transcript. */
sealed interface SpeechRecognitionState {
    data object Idle : SpeechRecognitionState
    data object Listening : SpeechRecognitionState
    data class Result(val text: String) : SpeechRecognitionState
    data class Error(val message: String) : SpeechRecognitionState
}
