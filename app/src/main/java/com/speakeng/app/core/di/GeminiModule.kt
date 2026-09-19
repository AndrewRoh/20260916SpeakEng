package com.speakeng.app.core.di

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.speakeng.app.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * SECURITY NOTE: the Gemini API key is bundled into the app via BuildConfig for this MVP.
 * Before shipping to production, move the request behind a backend proxy so the key never
 * ships inside the client binary.
 */
@Module
@InstallIn(SingletonComponent::class)
object GeminiModule {

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel = GenerativeModel(
        modelName = "gemini-flash-latest",
        apiKey = BuildConfig.GEMINI_API_KEY,
        systemInstruction = content {
            text(
                "You are a friendly, encouraging English conversation tutor. Keep replies short " +
                    "and natural, match the learner's level, and gently correct grammar mistakes by " +
                    "naturally rephrasing them in your reply instead of lecturing.",
            )
        },
    )
}
