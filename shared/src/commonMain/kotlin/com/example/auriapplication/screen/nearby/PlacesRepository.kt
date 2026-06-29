package com.example.auriapplication.screen.nearby

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PlacesRepository(private val placesApiKey: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getNearbyGiftStores(userLat: Double, userLng: Double): List<Place> {
        val requestBody = NearbySearchRequest(
            includedTypes = listOf(
                "beauty_salon", "book_store", "cake_shop", "cosmetics_store",
                "clothing_store", "electronics_store", "florist", "gift_shop",
                "jewelry_store", "liquor_store", "pet_store", "shoe_store",
                "sportswear_store", "tea_store", "thrift_store", "toy_store",
                "shopping_mall", "department_store", "supermarket", "grocery_store",
                "home_goods_store", "convenience_store"
            ),
            maxResultCount = 20,
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLng(userLat, userLng),
                    radius = 5000.0
                )
            )
        )

        return try {
            val response = client.post("https://places.googleapis.com/v1/places:searchNearby") {
                contentType(ContentType.Application.Json)
                header("X-Goog-Api-Key", placesApiKey)
                header(HttpHeaders.AcceptEncoding, "identity")
                header(
                    "X-Goog-FieldMask",
                    "places.displayName,places.id,places.formattedAddress,places.location,places.types,places.rating,places.userRatingCount,places.priceLevel,places.priceRange,places.photos.name"
                )
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                val placesResponse: PlacesResponse = response.body()
                placesResponse.places ?: emptyList()
            } else {
                println("API ERROR getNearbyGiftStores: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            println("EXCEPTION getNearbyGiftStores: ${e.message}")
            emptyList()
        }
    }

    suspend fun getNearbyPopularStores(userLat: Double, userLng: Double): List<Place> {
        val requestBody = NearbySearchRequest(
            includedTypes = listOf(
                "clothing_store",
                "bakery",
                "jewelry_store",
                "electronics_store",
                "shoe_store",
                "gift_shop",
                "book_store",
                "cosmetics_store",
                "liquor_store",
                "pet_store",
                "sportswear_store",
                "tea_store",
                "thrift_store",
                "toy_store"
            ),
            maxResultCount = 15,
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLng(userLat, userLng),
                    radius = 10000.0
                )
            )
        )

        return try {
            val response = client.post("https://places.googleapis.com/v1/places:searchNearby") {
                contentType(ContentType.Application.Json)
                header("X-Goog-Api-Key", placesApiKey)
                header(HttpHeaders.AcceptEncoding, "identity")
                header(
                    "X-Goog-FieldMask",
                    "places.displayName,places.id,places.formattedAddress,places.location,places.rating,places.userRatingCount,places.priceLevel,places.priceRange,places.photos.name,places.types"
                )
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                val placesResponse: PlacesResponse = response.body()
                // Sort by rating and user rating count to show "trending" ones
                placesResponse.places
                    ?.sortedWith(
                        compareByDescending<Place> { it.rating ?: 0.0 }
                            .thenByDescending { it.userRatingCount ?: 0 }
                    ) ?: emptyList()
            } else {
                println("API ERROR getTrendingBrandStores: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            println("EXCEPTION getTrendingBrandStores: ${e.message}")
            emptyList()
        }
    }

    suspend fun getNearbyRestaurants(userLat: Double, userLng: Double): List<Place> {
        val requestBody = NearbySearchRequest(
            includedTypes = listOf("restaurant", "cafe"),
            maxResultCount = 10,
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLng(userLat, userLng),
                    radius = 5000.0
                )
            )
        )

        return try {
            val response = client.post("https://places.googleapis.com/v1/places:searchNearby") {
                contentType(ContentType.Application.Json)
                header("X-Goog-Api-Key", placesApiKey)
                header(HttpHeaders.AcceptEncoding, "identity")
                header(
                    "X-Goog-FieldMask",
                    "places.displayName,places.id,places.formattedAddress,places.location,places.rating,places.userRatingCount,places.priceLevel,places.priceRange,places.photos.name,places.types"
                )
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                val placesResponse: PlacesResponse = response.body()
                placesResponse.places
                    ?.filter { place -> 
                        val types = place.types ?: emptyList()
                        !types.contains("movie_theater") && !types.contains("ice_cream_shop")
                    }
                    ?.sortedByDescending { it.rating ?: 0.0 } ?: emptyList()
            } else {
                println("API ERROR getNearbyRestaurants: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            println("EXCEPTION getNearbyRestaurants: ${e.message}")
            emptyList()
        }
    }

    suspend fun getFunActivitiesNearby(userLat: Double, userLng: Double): List<Place> {
        val requestBody = NearbySearchRequest(
            includedTypes = listOf(
                "movie_theater", "ice_cream_shop", "museum", "art_gallery", "amusement_center",
                "amusement_park", "bowling_alley", "zoo", "tourist_attraction",
                "park", "aquarium", "botanical_garden"
            ),
            maxResultCount = 20,
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLng(userLat, userLng),
                    radius = 10000.0
                )
            )
        )

        return try {
            val response = client.post("https://places.googleapis.com/v1/places:searchNearby") {
                contentType(ContentType.Application.Json)
                header("X-Goog-Api-Key", placesApiKey)
                header(HttpHeaders.AcceptEncoding, "identity")
                header(
                    "X-Goog-FieldMask",
                    "places.displayName,places.id,places.formattedAddress,places.location,places.rating,places.userRatingCount,places.priceLevel,places.priceRange,places.photos.name,places.types"
                )
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                val placesResponse: PlacesResponse = response.body()
                placesResponse.places
                    ?.filter { place -> 
                        val types = place.types ?: emptyList()
                        !types.contains("movie_theater") && !types.contains("ice_cream_shop")
                    }
                    ?.sortedByDescending { it.rating ?: 0.0 } ?: emptyList()
            } else {
                println("API ERROR getFunActivitiesNearby: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            println("EXCEPTION getFunActivitiesNearby: ${e.message}")
            emptyList()
        }
    }

    suspend fun getStudentStudySpots(userLat: Double, userLng: Double): List<Place> {
        val requestBody = NearbySearchRequest(
            includedTypes = listOf("cafe", "library", "coffee_shop"),
            maxResultCount = 10,
            locationRestriction = LocationRestriction(
                circle = Circle(
                    center = LatLng(userLat, userLng),
                    radius = 5000.0
                )
            )
        )

        return try {
            val response = client.post("https://places.googleapis.com/v1/places:searchNearby") {
                contentType(ContentType.Application.Json)
                header("X-Goog-Api-Key", placesApiKey)
                header(HttpHeaders.AcceptEncoding, "identity")
                header(
                    "X-Goog-FieldMask",
                    "places.displayName,places.id,places.formattedAddress,places.location,places.rating,places.userRatingCount,places.priceLevel,places.priceRange,places.photos.name,places.types"
                )
                setBody(requestBody)
            }

            if (response.status.isSuccess()) {
                val placesResponse: PlacesResponse = response.body()
                placesResponse.places?.sortedByDescending { it.rating ?: 0.0 } ?: emptyList()
            } else {
                println("API ERROR getStudentStudySpots: ${response.status}")
                emptyList()
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            println("EXCEPTION getStudentStudySpots: ${e.message}")
            emptyList()
        }
    }
}

fun calculateDistance(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val earthRadius = 6371000.0 // meters
    val dLat = (lat2 - lat1) * kotlin.math.PI / 180.0
    val dLon = (lon2 - lon1) * kotlin.math.PI / 180.0
    val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
            kotlin.math.cos(lat1 * kotlin.math.PI / 180.0) * kotlin.math.cos(lat2 * kotlin.math.PI / 180.0) *
            kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
    val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    return earthRadius * c
}
