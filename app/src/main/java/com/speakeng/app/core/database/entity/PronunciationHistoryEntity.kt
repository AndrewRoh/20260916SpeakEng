package com.speakeng.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pronunciation_history")
data class PronunciationHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: String,
    val bookTitle: String,
    val sentenceIndex: Int,
    val sentenceText: String,
    val recognizedText: String,
    val accuracy: Int,
    val timestamp: Long,
)
