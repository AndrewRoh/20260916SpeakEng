package com.speakeng.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Minimal Room entity skeleton for local conversation history.
 * TODO(Phase 4): flesh out fields and add curriculum/pronunciation entities when
 * local persistence is implemented.
 */
@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val isFromUser: Boolean,
    val timestamp: Long,
)
