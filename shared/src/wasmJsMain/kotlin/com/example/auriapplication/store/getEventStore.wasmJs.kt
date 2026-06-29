package com.example.auriapplication.store

import com.example.auriapplication.screen.home.GiftEvent
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storeOf
import io.github.xxfast.kstore.Codec
import kotlinx.coroutines.flow.MutableStateFlow

class MemoryCodec<T : Any>(default: T) : Codec<T> {
    private val state = MutableStateFlow<T?>(default)
    override suspend fun decode(): T? = state.value
    override suspend fun encode(value: T?) { state.value = value }
}

actual fun getEventStore(): KStore<List<GiftEvent>> = storeOf(
    codec = MemoryCodec(emptyList()),
    default = emptyList()
)
