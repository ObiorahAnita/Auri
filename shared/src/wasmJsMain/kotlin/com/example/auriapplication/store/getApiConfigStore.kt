package com.example.auriapplication.store

import com.example.auriapplication.store.ApiConfig
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.storeOf

actual fun getApiConfigStore(): KStore<ApiConfig> = storeOf(
	codec = MemoryCodec(ApiConfig()),
	default = ApiConfig()
)

