package com.speakeng.app.feature.profile.data.repository

import com.speakeng.app.core.database.dao.ConversationDao
import com.speakeng.app.core.database.dao.PronunciationHistoryDao
import com.speakeng.app.core.database.entity.ConversationEntity
import com.speakeng.app.core.database.entity.PronunciationHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProfileRepositoryImplTest {

    private class FakeConversationDao(initial: List<ConversationEntity> = emptyList()) : ConversationDao {
        private val state = MutableStateFlow(initial)
        override fun observeAll(): Flow<List<ConversationEntity>> = state
        override suspend fun insert(entity: ConversationEntity) {
            state.value = state.value + entity
        }
        override suspend fun countAiReplies(): Int = state.value.count { !it.isFromUser }
    }

    private class FakePronunciationHistoryDao(initial: List<PronunciationHistoryEntity> = emptyList()) : PronunciationHistoryDao {
        private val state = MutableStateFlow(initial)
        override fun observeAll(): Flow<List<PronunciationHistoryEntity>> = state
        override suspend fun insert(entity: PronunciationHistoryEntity) {
            state.value = state.value + entity
        }
        override suspend fun countAll(): Int = state.value.size
        override suspend fun averageAccuracy(): Double? =
            state.value.map { it.accuracy }.takeIf { it.isNotEmpty() }?.average()
    }

    @Test
    fun `getUserProfile returns zeroed stats when there is no history yet`() = runTest {
        val repository = ProfileRepositoryImpl(FakeConversationDao(), FakePronunciationHistoryDao())

        val profile = repository.getUserProfile().getOrThrow()

        assertEquals(0, profile.totalConversations)
        assertEquals(0, profile.totalMinutesPracticed)
        assertEquals(0, profile.averagePronunciationScore)
    }

    @Test
    fun `getUserProfile aggregates real conversation and pronunciation history`() = runTest {
        val conversationDao = FakeConversationDao(
            listOf(
                ConversationEntity(id = 1, message = "Hi", isFromUser = true, timestamp = 1L),
                ConversationEntity(id = 2, message = "Hello!", isFromUser = false, timestamp = 2L),
            ),
        )
        val pronunciationDao = FakePronunciationHistoryDao(
            listOf(
                PronunciationHistoryEntity(1, "b1", "Book", 0, "I have a dog.", "I have a dog", 100, 1L),
                PronunciationHistoryEntity(2, "b1", "Book", 1, "His name is Max.", "His name is Max", 60, 2L),
            ),
        )
        val repository = ProfileRepositoryImpl(conversationDao, pronunciationDao)

        val profile = repository.getUserProfile().getOrThrow()

        assertEquals(1, profile.totalConversations)
        assertEquals(80, profile.averagePronunciationScore)
    }
}
