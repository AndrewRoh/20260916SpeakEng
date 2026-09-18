package com.speakeng.app.feature.pronunciation.domain.model

/** Lifecycle events for a text-to-speech utterance, keyed by the utteranceId passed to `speak`. */
sealed interface SpeechEvent {
    val utteranceId: String

    data class Started(override val utteranceId: String) : SpeechEvent
    data class Completed(override val utteranceId: String) : SpeechEvent
    data class Failed(override val utteranceId: String) : SpeechEvent
}
