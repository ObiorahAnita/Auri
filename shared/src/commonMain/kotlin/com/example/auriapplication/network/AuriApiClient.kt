package com.example.auriapplication.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import com.example.auriapplication.screen.chatbot.GiftRecommendationService
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * Wrapper around the existing GiftRecommendationService to centralize baseUrl configuration.
 *
 * Create with:
 * - AuriApiClient(httpClient) — uses platform-specific default
 * - AuriApiClient(httpClient, "http://192.168.1.25:8081") — explicit override for real device
 *
 * Defaults:
 * - Android emulator: "http://10.0.2.2:8081"
 * - iOS simulator / desktop: "http://localhost:8081"
 * - Real iOS device: pass your machine's LAN IP
 * - Real Android device: pass your machine's LAN IP
 */
class AuriApiClient(private val httpClient: HttpClient, private val baseUrl: String = defaultBackendUrl()) {
    suspend fun getChatResponse(prompt: String) = GiftRecommendationService(httpClient).getGiftRecommendation(prompt, baseUrl)

    @Serializable
    data class HealthResponse(
        val status: String = "ok",
        val time: String? = null
    )

    data class HealthCheckResult(
        val isHealthy: Boolean,
        val status: String,
        val time: String? = null,
        val rawBody: String? = null
    )

    suspend fun checkHealth(): Result<HealthCheckResult> {
        return try {
            val url = "${baseUrl.trimEnd('/')}/api/health"
            val response = httpClient.get(url)
            if (response.status.value in 200..299) {
                val raw = response.body<String>()
                val parsed = runCatching {
                    Json { ignoreUnknownKeys = true }.decodeFromString(HealthResponse.serializer(), raw)
                }.getOrNull()

                Result.success(
                    HealthCheckResult(
                        isHealthy = true,
                        status = parsed?.status ?: raw.ifBlank { "ok" },
                        time = parsed?.time,
                        rawBody = raw
                    )
                )
            } else {
                Result.failure(Exception("Health check failed with status ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}



