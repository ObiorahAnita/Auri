package com.example.auriapplication.screen.nearby

import kotlinx.serialization.Serializable

@Serializable
data class NearbySearchRequest(
    val includedTypes: List<String>,
    val maxResultCount: Int,
    val locationRestriction: LocationRestriction
)

@Serializable
data class LocationRestriction(
    val circle: Circle
)

@Serializable
data class Circle(
    val center: LatLng,
    val radius: Double
)

@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class PlacesResponse(
    val places: List<Place>? = null
)

@Serializable
data class Place(
    val displayName: LocalizedText? = null,
    val id: String? = null,
    val formattedAddress: String? = null,
    val location: LatLng? = null,
    val types: List<String>? = null,
    val rating: Double? = null,
    val userRatingCount: Int? = null,
    val priceLevel: String? = null,
    val priceRange: PriceRange? = null,
    val photos: List<StorePhotoReference>? = null
)

@Serializable
data class StorePhotoReference(
    val name: String? = null
)

@Serializable
data class LocalizedText(
    val text: String
)

@Serializable
data class PriceRange(
    val startPrice: Money? = null,
    val endPrice: Money? = null,
)

@Serializable
data class Money(
    val currencyCode: String? = null,
    val units: Long? = null,
    val nanos: Int? = null,
)
