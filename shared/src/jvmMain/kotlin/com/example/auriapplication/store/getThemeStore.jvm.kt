package com.example.auriapplication.store

import io.github.xxfast.kstore.KStore
import io.github.xxfast.kstore.file.storeOf
import kotlinx.io.files.Path

actual fun getThemeStore(): KStore<Boolean> {
    val path = Path(System.getProperty("java.io.tmpdir"), "theme_preference.json")
    return storeOf(file = path)
}
