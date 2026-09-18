package com.speakeng.app.feature.pronunciation.data.repository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.model.SpeechRecognitionState
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow

class SpeechRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : SpeechRepository {

    private var activeRecognizer: SpeechRecognizer? = null

    private val ttsEvents = MutableSharedFlow<SpeechEvent>(extraBufferCapacity = 16)
    private var isReady = false

    private val textToSpeech: TextToSpeech = TextToSpeech(context) { status ->
        isReady = status == TextToSpeech.SUCCESS
        if (isReady) {
            textToSpeech.language = Locale.US
        }
    }.also { engine ->
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {
                ttsEvents.tryEmit(SpeechEvent.Started(utteranceId))
            }

            override fun onDone(utteranceId: String) {
                ttsEvents.tryEmit(SpeechEvent.Completed(utteranceId))
            }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun onError(utteranceId: String) {
                ttsEvents.tryEmit(SpeechEvent.Failed(utteranceId))
            }
        })
    }

    // NOTE: on-device recognition accuracy varies a lot by device/OS version, and the network
    // (cloud) recognizer needs connectivity, so real-world results are inherently noisy.
    override fun startListening(): Flow<SpeechRecognitionState> = callbackFlow {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            trySend(SpeechRecognitionState.Error("Speech recognition is not available on this device"))
            close()
            return@callbackFlow
        }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        activeRecognizer = recognizer
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit

            override fun onError(error: Int) {
                trySend(SpeechRecognitionState.Error(describeRecognizerError(error)))
                close()
            }

            override fun onResults(results: Bundle?) {
                val text = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    .orEmpty()
                trySend(SpeechRecognitionState.Result(text))
                close()
            }
        })

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
        }
        trySend(SpeechRecognitionState.Listening)
        recognizer.startListening(intent)

        awaitClose {
            recognizer.destroy()
            if (activeRecognizer === recognizer) activeRecognizer = null
        }
    }

    override fun stopListening() {
        activeRecognizer?.stopListening()
    }

    private fun describeRecognizerError(error: Int): String = when (error) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "Could not recognize speech, please try again"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer is busy, please try again"
        SpeechRecognizer.ERROR_SERVER -> "Server error"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
        else -> "Unknown speech recognition error"
    }

    override fun speak(text: String, utteranceId: String, rate: Float) {
        if (!isReady) {
            ttsEvents.tryEmit(SpeechEvent.Failed(utteranceId))
            return
        }
        textToSpeech.setSpeechRate(rate)
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    override fun stop() {
        textToSpeech.stop()
    }

    override fun ttsEvents(): Flow<SpeechEvent> = ttsEvents.asSharedFlow()
}
