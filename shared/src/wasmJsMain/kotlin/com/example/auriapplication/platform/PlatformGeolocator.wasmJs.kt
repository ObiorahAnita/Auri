package com.example.auriapplication.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.browser
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.NotSupportedPlatformGeocoder
import dev.jordond.compass.geocoder.GeocoderResult
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place

@Composable
actual fun rememberGeolocator(): Geolocator = remember { Geolocator.browser() }

@Composable
actual fun rememberGeocoder(): Geocoder = remember {
    object : Geocoder {
        override val platformGeocoder: PlatformGeocoder = NotSupportedPlatformGeocoder
        override fun isAvailable(): Boolean = false
        override suspend fun forward(address: String): GeocoderResult<Coordinates> = GeocoderResult.NotFound
        override suspend fun reverse(latitude: Double, longitude: Double): GeocoderResult<Place> = GeocoderResult.NotFound
    }
}
