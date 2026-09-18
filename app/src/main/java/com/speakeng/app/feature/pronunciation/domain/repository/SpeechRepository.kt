package com.speakeng.app.feature.pronunciation.domain.repository

import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.model.SpeechRecognitionState
import kotlinx.coroutines.flow.Flow

/** Speech-to-text / text-to-speech gateway, backed by Android's SpeechRecognizer and TextToSpeech. */
interface SpeechRepository {
    /** Starts listening and emits state changes until a [SpeechRecognitionState.Result] or [SpeechRecognitionState.Error]. */
    fun startListening(): Flow<SpeechRecognitionState>

    fun stopListening()

    /** Speaks [text] at [rate] (1f = normal speed), tagging the utterance as [utteranceId]. */
    fun speak(text: String, utteranceId: String, rate: Float = 1f)

    /** Stops any utterance currently being spoken. */
    fun stop()

    /** Lifecycle events (started/completed/failed) for utterances passed to [speak]. */
    fun ttsEvents(): Flow<SpeechEvent>
}
