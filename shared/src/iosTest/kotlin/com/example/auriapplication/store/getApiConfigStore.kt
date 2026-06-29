package com.example.auriapplication.store

import com.example.auriapplication.store.ApiConfig
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import java.io.File

actual fun getApiConfigStore(): KStore<ApiConfig> {
    val fileManager = NSFileManager.defaultManager()
    val documentDirectory = fileManager.URLsForDirectory(NSDocumentDirectory, inDomains = NSUserDomainMask).firstOrNull()
    val filePath = (documentDirectory?.path() ?: ".") + "/apiconfig-test.json"
    return storeOf(File(filePath), default = ApiConfig())
}

