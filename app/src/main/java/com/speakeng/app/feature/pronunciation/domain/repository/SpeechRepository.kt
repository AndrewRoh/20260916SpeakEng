package com.speakeng.app.feature.pronunciation.domain.repository

import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import kotlinx.coroutines.flow.Flow

/**
 * Speech-to-text / text-to-speech gateway.
 * TODO(Phase 3): implement STT natively with android.speech.SpeechRecognizer.
 * TTS is implemented for real (see SpeechRepositoryImpl) since the reading feature needs it.
 */
interface SpeechRepository {
    /** Emits partial/final recognized text while listening. */
    fun startListening(): Flow<String>

    fun stopListening()

    /** Speaks [text] at [rate] (1f = normal speed), tagging the utterance as [utteranceId]. */
    fun speak(text: String, utteranceId: String, rate: Float = 1f)

    /** Stops any utterance currently being spoken. */
    fun stop()

    /** Lifecycle events (started/completed/failed) for utterances passed to [speak]. */
    fun ttsEvents(): Flow<SpeechEvent>
}
