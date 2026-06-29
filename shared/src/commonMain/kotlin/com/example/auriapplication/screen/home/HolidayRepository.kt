package com.example.auriapplication.screen.home

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.serialization.Serializable
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.Clock

@Serializable
data class PublicHoliday(
    val date: String,
    val localName: String,
    val name: String,
    val countryCode: String,
    val fixed: Boolean,
    val global: Boolean,
    val types: List<String>? = null
)

class HolidayRepository(private val httpClient: HttpClient) {
    suspend fun fetchUpcomingHolidays(countryCode: String): List<PublicHoliday> {
        return try {
            val url = "https://date.nager.at/api/v3/NextPublicHolidays/$countryCode"
            println("Fetching holidays from: $url")
            val response = httpClient.get(url)
            println("Holiday API response status: ${response.status}")
            response.body<List<PublicHoliday>>()
        } catch (e: Exception) {
            println("Error fetching holidays: $e")
            emptyList()
        }
    }

    suspend fun fetchNextUpcomingHoliday(countryCode: String): PublicHoliday? {
        val holidays = fetchUpcomingHolidays(countryCode)
        val today = getCurrentDate()
        return holidays
            .filter { it.date >= today.toString() }
            .minByOrNull { it.date }
    }
}
