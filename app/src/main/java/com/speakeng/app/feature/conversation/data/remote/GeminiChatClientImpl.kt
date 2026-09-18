package com.speakeng.app.feature.conversation.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject

class GeminiChatClientImpl @Inject constructor(
    private val generativeModel: GenerativeModel,
) : GeminiChatClient {

    // A single chat session keeps multi-turn context for the lifetime of this (singleton) client.
    private val chat by lazy { generativeModel.startChat() }

    override suspend fun sendMessage(text: String): String {
        val response = chat.sendMessage(text)
        return response.text?.takeIf { it.isNotBlank() }
            ?: error("Gemini returned an empty response")
    }
}
