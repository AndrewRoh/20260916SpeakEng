package com.speakeng.app.feature.pronunciation.data.repository

import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/** TODO(Phase 2/3): back this with android.speech.SpeechRecognizer and android.speech.tts.TextToSpeech. */
class SpeechRepositoryImpl @Inject constructor() : SpeechRepository {

    override fun startListening(): Flow<String> = flow {
        throw NotImplementedError("SpeechRepository is a Phase 2/3 stub")
    }

    override fun stopListening() {
        // TODO(Phase 2/3): stop the native SpeechRecognizer session.
    }

    override suspend fun speak(text: String) {
        // TODO(Phase 2/3): delegate to TextToSpeech.
    }
}
