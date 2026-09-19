package com.speakeng.app.feature.conversation.data.repository

import com.speakeng.app.core.database.dao.ConversationDao
import com.speakeng.app.core.database.entity.ConversationEntity
import com.speakeng.app.feature.conversation.domain.model.ChatMessage
import com.speakeng.app.feature.conversation.domain.model.MessageSender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ConversationHistoryRepositoryImplTest {

    private class FakeConversationDao : ConversationDao {
        private val state = MutableStateFlow<List<ConversationEntity>>(emptyList())
        private var nextId = 1L

        override fun observeAll(): Flow<List<ConversationEntity>> = state

        override suspend fun insert(entity: ConversationEntity) {
            state.value = state.value + entity.copy(id = nextId++)
        }

        override suspend fun countAiReplies(): Int = state.value.count { !it.isFromUser }
    }

    @Test
    fun `saveMessage persists it and getMessages maps it back`() = runTest {
        val dao = FakeConversationDao()
        val repository = ConversationHistoryRepositoryImpl(dao)

        repository.saveMessage(ChatMessage(id = 1L, sender = MessageSender.USER, text = "Hello", timestamp = 100L))
        repository.saveMessage(ChatMessage(id = 2L, sender = MessageSender.AI, text = "Hi there!", timestamp = 200L))

        val messages = repository.getMessages().getOrThrow()
        assertEquals(2, messages.size)
        assertEquals(MessageSender.USER, messages[0].sender)
        assertEquals("Hello", messages[0].text)
        assertEquals(MessageSender.AI, messages[1].sender)
        assertEquals("Hi there!", messages[1].text)
    }
}
