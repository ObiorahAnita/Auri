package com.example.auriapplication.screen.chatbot

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable

// Align request/response with the Spring Boot endpoint: POST /api/chat { "prompt": "..." }
@Serializable
data class ChatRequest(val prompt: String)

@Serializable
data class ChatResponse(val response: String)

class GiftRecommendationService(private val httpClient: HttpClient) {
    /**
     * Calls the backend POST /api/chat endpoint and returns the ChatResponse.
     *
     * Notes:
     * - Android emulator: http://10.0.2.2:8081
     * - iOS simulator/desktop: http://localhost:8081
     * - Real device: use your machine LAN IP, e.g. http://192.168.1.25:8081
     */
    suspend fun getGiftRecommendation(prompt: String, baseUrl: String = "http://localhost:8081"): Result<ChatResponse> {
        return try {
            val url = "${baseUrl.trimEnd('/')}/api/chat"
            println("Sending chat request to: $url")

            val response = httpClient.post(url) {
                contentType(ContentType.Application.Json)
                setBody(ChatRequest(prompt))
            }

            println("Chat API response status: ${response.status}")

            if (response.status.value in 200..299) {
                Result.success(response.body<ChatResponse>())
            } else {
                Result.failure(Exception("Server returned status ${response.status}"))
            }
        } catch (e: Exception) {
            println("Error calling chat endpoint: $e")
            val mapped = if (e is SocketTimeoutException) {
                Exception("Request timed out. Check backend URL/network or try again.")
            } else {
                e
            }
            Result.failure(mapped)
        }
    }
}
