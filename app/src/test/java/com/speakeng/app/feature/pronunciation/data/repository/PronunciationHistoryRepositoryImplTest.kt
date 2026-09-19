package com.speakeng.app.feature.pronunciation.data.repository

import com.speakeng.app.core.database.dao.PronunciationHistoryDao
import com.speakeng.app.core.database.entity.PronunciationHistoryEntity
import com.speakeng.app.feature.pronunciation.domain.model.PronunciationHistoryRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PronunciationHistoryRepositoryImplTest {

    private class FakePronunciationHistoryDao : PronunciationHistoryDao {
        private val state = MutableStateFlow<List<PronunciationHistoryEntity>>(emptyList())
        private var nextId = 1L

        override fun observeAll(): Flow<List<PronunciationHistoryEntity>> = state

        override suspend fun insert(entity: PronunciationHistoryEntity) {
            state.value = state.value + entity.copy(id = nextId++)
        }

        override suspend fun countAll(): Int = state.value.size

        override suspend fun averageAccuracy(): Double? =
            state.value.map { it.accuracy }.takeIf { it.isNotEmpty() }?.average()
    }

    @Test
    fun `saveResult inserts a row the dao can be queried for`() = runTest {
        val dao = FakePronunciationHistoryDao()
        val repository = PronunciationHistoryRepositoryImpl(dao)

        val result = repository.saveResult(
            PronunciationHistoryRecord(
                bookId = "bundled:my_pet_dog.txt",
                bookTitle = "my pet dog",
                sentenceIndex = 0,
                sentenceText = "I have a pet dog.",
                recognizedText = "I have a pet dog",
                accuracy = 100,
                timestamp = 123L,
            ),
        )

        assertTrue(result.isSuccess)
        assertEquals(1, dao.countAll())
        assertEquals(100.0, dao.averageAccuracy())
    }
}
