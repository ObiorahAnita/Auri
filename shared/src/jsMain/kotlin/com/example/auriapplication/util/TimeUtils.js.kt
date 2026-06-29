package com.example.auriapplication.util

actual fun getEpochMillis(): Long = kotlin.js.Date.now().toLong()
