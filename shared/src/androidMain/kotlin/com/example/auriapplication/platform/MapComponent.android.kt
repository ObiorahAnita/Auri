package com.example.auriapplication.platform

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.platform.LocalContext
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import dev.jordond.compass.Coordinates

@Composable
actual fun NearByMap(
    modifier: Modifier,
    coordinates: Coordinates?,
    title: String?
) {
    val context = LocalContext.current
    val hasLocationPermission = remember(context) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    if (!hasLocationPermission) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Requesting location permission to display the map...")
        }
        return
    }

    if (coordinates == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Unable to display map, user location not found")
        }
        return
    }

    val userLocation = remember(coordinates) {
        LatLng(coordinates.latitude, coordinates.longitude)
    }

    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 12f)
    }

    val markerState = remember(userLocation) { MarkerState(position = userLocation) }

    // Update camera and show info window when coordinates change
    androidx.compose.runtime.LaunchedEffect(userLocation) {
        cameraPosition.animate(
            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(userLocation, 15f)
        )
        markerState.showInfoWindow()
    }

    var uiSettings = remember { MapUiSettings(
        zoomControlsEnabled = true,
        zoomGesturesEnabled = true,
        scrollGesturesEnabled = true,
        rotationGesturesEnabled = true,
        myLocationButtonEnabled = true)
    }
    var properties = remember { MapProperties(isMyLocationEnabled = true) }
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPosition,
        uiSettings = uiSettings,
        properties = properties
    )
    {
        Marker(
            state = markerState,
        )
    }
}
