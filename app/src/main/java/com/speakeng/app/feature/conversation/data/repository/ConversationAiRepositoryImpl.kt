package com.speakeng.app.feature.conversation.data.repository

import com.speakeng.app.feature.conversation.domain.repository.ConversationAiRepository
import javax.inject.Inject

/** TODO(Phase 2): call the Claude API here instead of returning a stub failure. */
class ConversationAiRepositoryImpl @Inject constructor() : ConversationAiRepository {
    override suspend fun sendMessage(text: String): Result<String> =
        Result.failure(NotImplementedError("ConversationAiRepository is a Phase 2 stub"))
}
