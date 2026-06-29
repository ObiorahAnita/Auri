package com.example.auriapplication.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.mobile
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.MobileGeocoder
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.autocomplete.MobileAutocomplete
import dev.jordond.compass.Place

@Composable
actual fun rememberGeolocator(): Geolocator = remember { Geolocator.mobile() }

@Composable
actual fun rememberGeocoder(): Geocoder = remember { MobileGeocoder() }

@Composable
actual fun rememberAutocomplete(): Autocomplete<Place> = remember { Autocomplete() }
