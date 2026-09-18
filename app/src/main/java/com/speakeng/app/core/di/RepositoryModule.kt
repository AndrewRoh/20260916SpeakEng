package com.speakeng.app.core.di

import com.speakeng.app.feature.conversation.data.remote.GeminiChatClient
import com.speakeng.app.feature.conversation.data.remote.GeminiChatClientImpl
import com.speakeng.app.feature.conversation.data.repository.ConversationAiRepositoryImpl
import com.speakeng.app.feature.conversation.domain.repository.ConversationAiRepository
import com.speakeng.app.feature.curriculum.data.repository.CurriculumRepositoryImpl
import com.speakeng.app.feature.curriculum.domain.repository.CurriculumRepository
import com.speakeng.app.feature.home.data.repository.HomeRepositoryImpl
import com.speakeng.app.feature.home.domain.repository.HomeRepository
import com.speakeng.app.feature.profile.data.repository.ProfileRepositoryImpl
import com.speakeng.app.feature.profile.domain.repository.ProfileRepository
import com.speakeng.app.feature.pronunciation.data.repository.SpeechRepositoryImpl
import com.speakeng.app.feature.pronunciation.domain.repository.SpeechRepository
import com.speakeng.app.feature.reading.data.datasource.AssetBundledBookSource
import com.speakeng.app.feature.reading.data.datasource.BundledBookSource
import com.speakeng.app.feature.reading.data.datasource.ContentResolverUploadSource
import com.speakeng.app.feature.reading.data.datasource.UploadSource
import com.speakeng.app.feature.reading.data.repository.BookRepositoryImpl
import com.speakeng.app.feature.reading.domain.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindConversationAiRepository(impl: ConversationAiRepositoryImpl): ConversationAiRepository

    @Binds
    @Singleton
    abstract fun bindGeminiChatClient(impl: GeminiChatClientImpl): GeminiChatClient

    @Binds
    @Singleton
    abstract fun bindSpeechRepository(impl: SpeechRepositoryImpl): SpeechRepository

    @Binds
    @Singleton
    abstract fun bindCurriculumRepository(impl: CurriculumRepositoryImpl): CurriculumRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindBookRepository(impl: BookRepositoryImpl): BookRepository

    @Binds
    abstract fun bindBundledBookSource(impl: AssetBundledBookSource): BundledBookSource

    @Binds
    abstract fun bindUploadSource(impl: ContentResolverUploadSource): UploadSource
}
