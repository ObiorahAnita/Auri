package com.example.auriapplication.store

import com.example.auriapplication.screen.home.GiftEvent
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path

actual fun getEventStore(): KStore<List<GiftEvent>> {
    val path = Path(System.getProperty("java.io.tmpdir"), "personal_events.json")
    return storeOf(file = path, default = emptyList())
}
