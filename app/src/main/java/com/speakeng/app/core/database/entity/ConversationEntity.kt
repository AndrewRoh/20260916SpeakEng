package com.speakeng.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val isFromUser: Boolean,
    val timestamp: Long,
)
