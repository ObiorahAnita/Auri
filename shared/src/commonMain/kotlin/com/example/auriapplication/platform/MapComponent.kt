package com.example.auriapplication.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.jordond.compass.Coordinates

@Composable
expect fun NearByMap(
    modifier: Modifier,
    coordinates: Coordinates? = null,
    title: String? = null
)
