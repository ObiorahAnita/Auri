package com.example.auriapplication.store

import com.example.auriapplication.store.ApiConfig
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.io.files.Path
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual fun getApiConfigStore(): KStore<ApiConfig> {
	val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
		directory = NSDocumentDirectory,
		inDomain = NSUserDomainMask,
		appropriateForURL = null,
		create = false,
		error = null
	)
	val path = Path(documentDirectory?.path!!, "api_config.json")
	return storeOf(file = path, default = ApiConfig())
}

