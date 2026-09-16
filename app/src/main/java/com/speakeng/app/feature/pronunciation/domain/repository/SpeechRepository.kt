package com.speakeng.app.feature.pronunciation.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Speech-to-text / text-to-speech gateway.
 * TODO(Phase 2/3): implement natively with Android SpeechRecognizer (STT) and
 * TextToSpeech (TTS).
 */
interface SpeechRepository {
    /** Emits partial/final recognized text while listening. */
    fun startListening(): Flow<String>

    fun stopListening()

    suspend fun speak(text: String)
}
