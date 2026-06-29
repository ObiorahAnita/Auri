package com.example.auriapplication.platform

import com.example.auriapplication.platform.Platform

class WasmPlatform: Platform {
    override val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = WasmPlatform()