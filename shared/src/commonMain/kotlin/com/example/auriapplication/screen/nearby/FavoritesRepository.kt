package com.example.auriapplication.screen.nearby

import com.example.auriapplication.store.getFavoritesStore
import io.github.xxfast.kstore.KStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(
    private val store: KStore<List<Place>> = getFavoritesStore()
) {
    val favorites: Flow<List<Place>> = store.updates.map { it ?: emptyList() }

    suspend fun toggleFavorite(place: Place) {
        val current = store.get() ?: emptyList()
        val exists = current.any { it.id == place.id }
        if (exists) {
            store.set(current.filter { it.id != place.id })
        } else {
            store.set(current + place)
        }
    }

    suspend fun isFavorite(placeId: String?): Boolean {
        if (placeId == null) return false
        val current = store.get() ?: emptyList()
        return current.any { it.id == placeId }
    }
}
