package com.speakeng.app.feature.conversation.data.repository

import com.speakeng.app.core.database.dao.ConversationDao
import com.speakeng.app.core.database.entity.ConversationEntity
import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.model.MessageSender
import com.speakeng.app.feature.conversation.domain.repository.ConversationHistoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class ConversationHistoryRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
) : ConversationHistoryRepository {

    override suspend fun getMessages(): Result<List<ChatMessage>> = runCatching {
        conversationDao.observeAll().first().map { it.toChatMessage() }
    }

    override suspend fun saveMessage(message: ChatMessage): Result<Unit> = runCatching {
        conversationDao.insert(
            ConversationEntity(
                message = message.text,
                isFromUser = message.sender == MessageSender.USER,
                timestamp = message.timestamp,
            ),
        )
    }

    private fun ConversationEntity.toChatMessage() = ChatMessage(
        id = id,
        sender = if (isFromUser) MessageSender.USER else MessageSender.AI,
        text = message,
        timestamp = timestamp,
    )
}
