package com.example.auriapplication.store

import io.github.xxfast.kstore.KStore
import kotlinx.serialization.Serializable

@Serializable
data class ApiConfig(
    val backendUrl: String = ""
)

expect fun getApiConfigStore(): KStore<ApiConfig>



