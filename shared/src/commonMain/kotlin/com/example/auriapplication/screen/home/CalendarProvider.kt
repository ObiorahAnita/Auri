package com.example.auriapplication.screen.home

import kotlinx.datetime.LocalDate

data class CalendarEvent(
    val id: String,
    val title: String,
    val date: LocalDate
)

expect fun getCurrentDate(): LocalDate

expect class CalendarProvider() {
    suspend fun fetchUpcomingEvents(): List<CalendarEvent>
}
