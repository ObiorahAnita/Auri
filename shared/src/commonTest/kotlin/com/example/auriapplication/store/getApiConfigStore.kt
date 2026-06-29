package com.example.auriapplication.store

import com.example.auriapplication.store.ApiConfig
import io.github.xxfast.kstore.KStore
import java.io.File

actual fun getApiConfigStore(): KStore<ApiConfig> {
    val file = File(System.getProperty("java.io.tmpdir"), "apiconfig-test.json")
    return storeOf(file, default = ApiConfig())
}

