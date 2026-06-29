package com.example.auriapplication.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
