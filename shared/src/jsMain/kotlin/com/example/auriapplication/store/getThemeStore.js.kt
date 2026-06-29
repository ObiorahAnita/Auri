package com.example.auriapplication.store

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storeOf

actual fun getThemeStore(): KStore<Boolean> = storeOf(
    codec = MemoryCodec(false)
)
