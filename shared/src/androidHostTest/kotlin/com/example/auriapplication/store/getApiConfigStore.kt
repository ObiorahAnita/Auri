package com.example.auriapplication.store

import com.example.auriapplication.store.ApiConfig
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import java.io.File

// AndroidHostTest implementation
actual fun getApiConfigStore(): KStore<ApiConfig> {
    val file = File(System.getProperty("java.io.tmpdir"), "apiconfig.json")
    return storeOf(file, default = ApiConfig())
}

