package com.example.auriapplication.store

import com.example.auriapplication.appContext
import com.example.auriapplication.store.ApiConfig
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path

actual fun getApiConfigStore(): KStore<ApiConfig> {
	val filesDir = appContext.filesDir.absolutePath
	val path = Path(filesDir, "api_config.json")
	return storeOf(file = path, default = ApiConfig())
}

