package com.example.auriapplication.screen.home

import platform.EventKit.EKEventStore
import platform.EventKit.EKEvent
import platform.Foundation.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate

actual fun getCurrentDate(): LocalDate {
    val nsDate = NSDate()
    val millis = (nsDate.timeIntervalSince1970 * 1000).toLong()
    return Instant.fromEpochMilliseconds(millis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .date
}

actual class CalendarProvider actual constructor() {
    private val eventStore = EKEventStore()

    actual suspend fun fetchUpcomingEvents(): List<CalendarEvent> {
        val startDate = NSDate()
        val endDate = NSDate.dateWithTimeIntervalSince1970(startDate.timeIntervalSince1970 + 30 * 24 * 60 * 60) // 30 days
        
        val predicate = eventStore.predicateForEventsWithStartDate(startDate, endDate, null)
        val ekEvents = eventStore.eventsMatchingPredicate(predicate) as List<EKEvent>
        
        return ekEvents.map { ekEvent ->
            val startMillis = (ekEvent.startDate?.timeIntervalSince1970 ?: 0.0).toLong() * 1000
            val date = Instant.fromEpochMilliseconds(startMillis)
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
                
            CalendarEvent(
                id = ekEvent.eventIdentifier ?: "",
                title = ekEvent.title ?: "",
                date = date
            )
        }
    }
}
