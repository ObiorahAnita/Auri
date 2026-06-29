package com.example.auriapplication.screen.home

import kotlinx.serialization.Serializable
import kotlinx.datetime.LocalDate

@Serializable
data class GiftEvent(
    val id: String,
    val name: String,
    val date: String, // ISO format YYYY-MM-DD
    val isPersonal: Boolean = true,
)

data class GiftBannerUiState(
    val isVisible: Boolean = false,
    val eventName: String = "",
    val daysUntilEvent: Int = 0,
    val isPersonal: Boolean = false,
    val eventDate: LocalDate? = null
)
