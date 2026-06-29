package com.example.auriapplication.screen.nearby

import kotlinx.serialization.Serializable

@Serializable
data class StoreDetailsRequest(
    val id: String? = null,
    val displayName: LocalizedText? = null,
    val formattedAddress: String? = null,
    val googleMapsUri: String? = null,
    val websiteUri: String? = null,
    val rating: Double? = null,
    val userRatingCount: Int? = null,
    
    val priceLevel: String? = null,
    val priceRange: PriceRange? = null,
    val currentOpeningHours: OpeningHours? = null,
    val delivery: Boolean? = null,
    val photos: List<StorePhoto>? = null,
    val reviews: List<Review>? = null,
    val editorialSummary: LocalizedText? = null,
)

@Serializable
data class OpeningHours(
    val openNow: Boolean? = null,
    val weekdayDescriptions: List<String>? = null,
    val periods: List<Periods>? = null,
)

@Serializable
data class Periods(
    val open: TimePoint? = null,
    val close: TimePoint? = null,
)

@Serializable
data class TimePoint(
    val day: Int? = null,
    val hour: Int? = null,
    val minute: Int? = null,
)

@Serializable
data class StorePhoto(
    val name: String? = null,
    val widthPx: Int? = null,
    val heightPx: Int? = null,
    val authorAttributions: List<AuthorAttributions>? = null,
)

@Serializable
data class Review(
    val name: String? = null,
    val relativePublishTimeDescription: String? = null,
    val rating: Double? = null,
    val text: LocalizedText? = null,
    val originalText: LocalizedText? = null,
    val authorAttribution: AuthorAttributions? = null,
)

@Serializable
data class AuthorAttributions(
    val displayName: String? = null,
    val uri: String? = null,
    val photoUri: String? = null,
)
