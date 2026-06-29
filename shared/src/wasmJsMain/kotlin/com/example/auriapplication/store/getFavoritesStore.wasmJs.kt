package com.example.auriapplication.store

import com.example.auriapplication.screen.nearby.Place
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storeOf

actual fun getFavoritesStore(): KStore<List<Place>> = storeOf(
    codec = MemoryCodec(emptyList()),
    default = emptyList()
)
