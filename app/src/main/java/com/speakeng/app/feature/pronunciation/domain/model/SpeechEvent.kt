package com.speakeng.app.feature.pronunciation.domain.model

/** Lifecycle events for a text-to-speech utterance, keyed by the utteranceId passed to `speak`. */
sealed interface SpeechEvent {
    data class Started(val utteranceId: String) : SpeechEvent
    data class Completed(val utteranceId: String) : SpeechEvent
    data class Failed(val utteranceId: String) : SpeechEvent
}
