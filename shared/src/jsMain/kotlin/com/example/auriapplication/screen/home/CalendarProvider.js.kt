package com.example.auriapplication.screen.home

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

actual fun getCurrentDate(): LocalDate = 
    Clock.System.todayIn(TimeZone.currentSystemDefault())

actual class CalendarProvider actual constructor() {
    actual suspend fun fetchUpcomingEvents(): List<CalendarEvent> = emptyList()
}
