package com.speakeng.app.feature.conversation.domain.model

enum class MessageSender { USER, AI }

data class ChatMessage(
    val id: Long,
    val sender: MessageSender,
    val text: String,
    val timestamp: Long,
)
