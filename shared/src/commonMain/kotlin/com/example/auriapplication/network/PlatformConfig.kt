package com.example.auriapplication.network

/**
 * Platform-specific default backend URL.
 * - Android emulator: http://10.0.2.2:8081
 * - iOS simulator / desktop: http://localhost:8081
 * For real devices, override at runtime with your machine's LAN IP.
 */
expect fun defaultBackendUrl(): String



