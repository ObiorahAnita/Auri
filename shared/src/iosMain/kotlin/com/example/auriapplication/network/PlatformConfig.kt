package com.example.auriapplication.network

// iOS simulator can use localhost; for real device, users must override with their machine's LAN IP
actual fun defaultBackendUrl(): String = "http://localhost:8081"




