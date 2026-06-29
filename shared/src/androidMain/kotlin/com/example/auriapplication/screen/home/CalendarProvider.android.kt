package com.example.auriapplication.screen.home

import android.provider.CalendarContract
import com.example.auriapplication.appContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.todayIn

actual fun getCurrentDate(): LocalDate = 
    Clock.System.todayIn(TimeZone.currentSystemDefault())

actual class CalendarProvider actual constructor() {
    actual suspend fun fetchUpcomingEvents(): List<CalendarEvent> {
        val events = mutableListOf<CalendarEvent>()
        val projection = arrayOf(
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART
        )

        val selection = "${CalendarContract.Events.DTSTART} >= ?"
        val selectionArgs = arrayOf(System.currentTimeMillis().toString())
        val sortOrder = "${CalendarContract.Events.DTSTART} ASC"

        appContext.contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idIndex = cursor.getColumnIndex(CalendarContract.Events._ID)
            val titleIndex = cursor.getColumnIndex(CalendarContract.Events.TITLE)
            val startIndex = cursor.getColumnIndex(CalendarContract.Events.DTSTART)

            while (cursor.moveToNext()) {
                val id = cursor.getString(idIndex)
                val title = cursor.getString(titleIndex)
                val startMillis = cursor.getLong(startIndex)
                
                val date = Instant.fromEpochMilliseconds(startMillis)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .date

                events.add(CalendarEvent(id, title, date))
            }
        }
        return events
    }
}
