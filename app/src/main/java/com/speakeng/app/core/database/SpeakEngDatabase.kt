package com.speakeng.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.speakeng.app.core.database.dao.ConversationDao
import com.speakeng.app.core.database.dao.CurriculumProgressDao
import com.speakeng.app.core.database.dao.PronunciationHistoryDao
import com.speakeng.app.core.database.entity.ConversationEntity
import com.speakeng.app.core.database.entity.CurriculumProgressEntity
import com.speakeng.app.core.database.entity.PronunciationHistoryEntity

@Database(
    entities = [ConversationEntity::class, CurriculumProgressEntity::class, PronunciationHistoryEntity::class],
    version = 2,
    exportSchema = true,
)
abstract class SpeakEngDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun curriculumProgressDao(): CurriculumProgressDao
    abstract fun pronunciationHistoryDao(): PronunciationHistoryDao
}
