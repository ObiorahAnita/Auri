package com.example.auriapplication.screen.chatbot

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = 0L
)
