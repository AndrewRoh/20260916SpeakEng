package com.speakeng.app.core.di

import android.content.Context
import androidx.room.Room
import com.speakeng.app.core.database.SpeakEngDatabase
import com.speakeng.app.core.database.dao.ConversationDao
import com.speakeng.app.core.database.dao.CurriculumProgressDao
import com.speakeng.app.core.database.dao.PronunciationHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * No migrations exist yet (pre-release debug app, no real user data to preserve), so a
     * schema version bump destructively recreates local tables instead of crashing on open.
     */
    @Provides
    @Singleton
    fun provideSpeakEngDatabase(@ApplicationContext context: Context): SpeakEngDatabase =
        Room.databaseBuilder(context, SpeakEngDatabase::class.java, "speakeng.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideConversationDao(database: SpeakEngDatabase): ConversationDao =
        database.conversationDao()

    @Provides
    fun provideCurriculumProgressDao(database: SpeakEngDatabase): CurriculumProgressDao =
        database.curriculumProgressDao()

    @Provides
    fun providePronunciationHistoryDao(database: SpeakEngDatabase): PronunciationHistoryDao =
        database.pronunciationHistoryDao()
}
