package com.example.auriapplication.platform

import androidx.compose.runtime.Composable
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.autocomplete.Autocomplete
import dev.jordond.compass.Place

@Composable
expect fun rememberGeolocator(): Geolocator

@Composable
expect fun rememberGeocoder(): Geocoder

@Composable
expect fun rememberAutocomplete(): Autocomplete<Place>
