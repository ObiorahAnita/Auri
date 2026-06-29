package com.example.auriapplication.store

import com.example.auriapplication.appContext
import com.example.auriapplication.screen.home.GiftEvent
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path

// Pass your Android context during app initialization

actual fun getEventStore(): KStore<List<GiftEvent>> {
    val filesDir = appContext.filesDir.absolutePath
    val path = Path(filesDir, "personal_events.json")
    return storeOf(file = path, default = emptyList())
}
