package com.speakeng.app.feature.pronunciation.data.repository

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.speakeng.app.feature.pronunciation.domain.model.SpeechEvent
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow

/** TODO(Phase 3): implement STT with android.speech.SpeechRecognizer. TTS below is a real implementation. */
class SpeechRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
) : SpeechRepository {

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

            @Deprecated("Deprecated in Java", ReplaceWith(""))
            override fun onError(utteranceId: String) {
                ttsEvents.tryEmit(SpeechEvent.Failed(utteranceId))
            }
        })
    }

    override fun startListening(): Flow<String> = flow {
        throw NotImplementedError("SpeechRepository STT is a Phase 3 stub")
    }

    override fun stopListening() {
        // TODO(Phase 3): stop the native SpeechRecognizer session.
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
