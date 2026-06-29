package com.example.auriapplication.store

import com.example.auriapplication.appContext
import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path

actual fun getThemeStore(): KStore<Boolean> {
    val filesDir = appContext.filesDir.absolutePath
    val path = Path(filesDir, "theme_preference.json")
    return storeOf(file = path)
}
