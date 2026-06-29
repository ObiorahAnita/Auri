package com.example.auriapplication.network

import com.example.auriapplication.store.getApiConfigStore

/**
 * Get the effective backend URL considering:
 * 1. User's saved configuration (if set)
 * 2. Platform default (if not set)
 */
suspend fun getEffectiveBackendUrl(): String {
    return try {
        val config = getApiConfigStore().get()
        if (config?.backendUrl?.isNotBlank() == true) {
            config.backendUrl.trim()
        } else {
            defaultBackendUrl()
        }
    } catch (_: Exception) {
        defaultBackendUrl()
    }
}





