package com.example.auriapplication

import androidx.compose.ui.window.ComposeUIViewController
import cocoapods.GoogleMaps.GMSServices
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
fun MainViewController(): UIViewController {
    GMSServices.provideAPIKey("AIzaSyCurf0k1ZnYq6PmqEbUrObjuT2HD0a6hL8")
    return ComposeUIViewController { App() }
}
