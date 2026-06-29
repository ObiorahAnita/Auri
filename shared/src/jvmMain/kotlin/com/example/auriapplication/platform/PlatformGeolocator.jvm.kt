package com.example.auriapplication.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

import dev.jordond.compass.geocoder.*
import dev.jordond.compass.Place
import dev.jordond.compass.Coordinates
import dev.jordond.compass.autocomplete.*

@Composable
actual fun rememberGeolocator(): Geolocator = remember {
    object : Geolocator {
        override val trackingStatus: Flow<TrackingStatus> = MutableStateFlow(TrackingStatus.Idle)
        override suspend fun lastLocation(priority: Priority): GeolocatorResult = GeolocatorResult.NotFound
        override suspend fun lastLocation(request: LocationRequest): GeolocatorResult = GeolocatorResult.NotFound
        override suspend fun isAvailable(): Boolean = false
        override suspend fun current(priority: Priority): GeolocatorResult = GeolocatorResult.NotFound
        override suspend fun current(request: LocationRequest): GeolocatorResult = GeolocatorResult.NotFound
        override fun track(request: LocationRequest): Flow<TrackingStatus> = trackingStatus
        override fun stopTracking() {}
    }
}

@Composable
actual fun rememberGeocoder(): Geocoder = remember {
    object : Geocoder {
        override val platformGeocoder: PlatformGeocoder = NotSupportedPlatformGeocoder
        override fun isAvailable(): Boolean = false
        override suspend fun forward(address: String): GeocoderResult<Coordinates> = GeocoderResult.NotFound
        override suspend fun reverse(latitude: Double, longitude: Double): GeocoderResult<Place> = GeocoderResult.NotFound
    }
}

@Composable
actual fun rememberAutocomplete(): Autocomplete<Place> = remember {
    object : Autocomplete<Place> {
        override val options: AutocompleteOptions = AutocompleteOptions()
        override suspend fun search(query: String): AutocompleteResult<Place> = AutocompleteResult.NotSupported
    }
}
