package com.speakeng.app.core.di

import android.content.Context
import androidx.room.Room
import com.speakeng.app.core.database.SpeakEngDatabase
import com.speakeng.app.core.database.dao.ConversationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSpeakEngDatabase(@ApplicationContext context: Context): SpeakEngDatabase =
        Room.databaseBuilder(context, SpeakEngDatabase::class.java, "speakeng.db").build()

    @Provides
    fun provideConversationDao(database: SpeakEngDatabase): ConversationDao =
        database.conversationDao()
}
