package com.example.auriapplication.screen.nearby

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class StoreDetailsRepository(
    private val placesApiKey: String
) {
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
    }

    suspend fun getStoreDetails(storeId: String): StoreDetailsRequest? {
        val url = "https://places.googleapis.com/v1/places/$storeId"
        // Explicitly requesting photos subfields to ensure photo name is returned
        val fieldMask = "id,displayName,formattedAddress,googleMapsUri,websiteUri,rating,userRatingCount,priceLevel,priceRange,currentOpeningHours,delivery,photos.name,photos.widthPx,photos.heightPx,reviews,editorialSummary"

        return try {
            val response = httpClient.get(url) {
                header("X-Goog-Api-Key", placesApiKey)
                header("X-Goog-FieldMask", fieldMask)
                header(HttpHeaders.AcceptEncoding, "identity")
                contentType(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                response.body<StoreDetailsRequest>()
            } else {
                println("Error fetching details: ${response.status}")
                null
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            e.printStackTrace()
            null
        }
    }

    fun getPhotoUrl(photoName: String, maxWidth: Int = 800): String {
        return "https://places.googleapis.com/v1/$photoName/media?key=$placesApiKey&maxWidthPx=$maxWidth"
    }

    suspend fun fetchPhotoBytes(photoName: String): ByteArray? {
        val url = getPhotoUrl(photoName)
        return try {
            val response = httpClient.get(url) {
                header(HttpHeaders.AcceptEncoding, "identity")
            }
            if (response.status.isSuccess()) {
                response.body<ByteArray>()
            } else {
                println("Error fetching photo bytes: ${response.status}")
                null
            }
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            e.printStackTrace()
            null
        }
    }
}

