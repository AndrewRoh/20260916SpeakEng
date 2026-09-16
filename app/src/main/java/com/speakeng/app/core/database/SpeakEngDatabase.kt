package com.speakeng.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.speakeng.app.core.database.dao.ConversationDao
import com.speakeng.app.core.database.entity.ConversationEntity

/**
 * App-wide Room database. Phase 1 ships only the schema skeleton; Phase 4 will
 * add the remaining entities (curriculum progress, pronunciation history, etc.)
 * and start reading/writing through it.
 */
@Database(entities = [ConversationEntity::class], version = 1, exportSchema = true)
abstract class SpeakEngDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
}
