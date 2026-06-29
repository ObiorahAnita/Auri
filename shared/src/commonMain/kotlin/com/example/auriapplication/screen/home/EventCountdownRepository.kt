package com.example.auriapplication.screen.home

import kotlinx.datetime.*

class EventCountdownRepository(
    private val holidayRepository: HolidayRepository,
    private val calendarProvider: CalendarProvider
) {
    suspend fun getUpcomingEventBannerState(countryCode: String): GiftBannerUiState {
        val today = getCurrentDate()
        println("EventRepo: Calculating banner state for $countryCode, today: $today")

        val nextHoliday = holidayRepository.fetchNextUpcomingHoliday(countryCode)
        println("EventRepo: Next holiday for $countryCode: $nextHoliday")

        val calendarEvents = try {
            calendarProvider.fetchUpcomingEvents()
        } catch (e: Exception) {
            println("EventRepo: Calendar fetch error: $e")
            emptyList()
        }
        val nextCalendar = calendarEvents
            .filter { it.date >= today }
            .minByOrNull { it.date }
        println("EventRepo: Next calendar event: $nextCalendar")

        val candidates = mutableListOf<Pair<String, LocalDate>>()
        
        nextHoliday?.let { candidates.add(it.name to LocalDate.parse(it.date)) }
        nextCalendar?.let { candidates.add(it.title to it.date) }
        
        val soonest = candidates.minByOrNull { it.second }
        
        return if (soonest != null) {
            val days = today.daysUntil(soonest.second)
            println("EventRepo: Final state - Event: ${soonest.first}, Days until: $days")
            GiftBannerUiState(
                isVisible = true,
                eventName = soonest.first,
                daysUntilEvent = days,
                isPersonal = soonest.first == nextCalendar?.title,
                eventDate = soonest.second
            )
        } else {
            println("EventRepo: No upcoming events found for $countryCode")
            GiftBannerUiState(isVisible = false)
        }
    }
}
