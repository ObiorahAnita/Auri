package com.example.auriapplication.store

import com.example.auriapplication.screen.nearby.Place
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path

actual fun getFavoritesStore(): KStore<List<Place>> {
    val path = Path(System.getProperty("java.io.tmpdir"), "favorite_places.json")
    return storeOf(file = path, default = emptyList())
}
