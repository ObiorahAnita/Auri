package com.example.auriapplication.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.jordond.compass.Coordinates

@Composable
actual fun NearByMap(
    modifier: Modifier,
    coordinates: Coordinates?,
    title: String?
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("Map not available on Wasm yet")
    }
}
