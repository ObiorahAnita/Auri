package com.example.auriapplication.store

import com.example.auriapplication.store.ApiConfig
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import java.io.File

actual fun getApiConfigStore(): KStore<ApiConfig> {
    val file = File(System.getProperty("user.home"), ".auri/apiconfig.json")
    file.parentFile?.mkdirs()
    return storeOf(file, default = ApiConfig())
}

