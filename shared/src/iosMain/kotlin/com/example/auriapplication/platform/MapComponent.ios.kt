package com.example.auriapplication.platform

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView
import cocoapods.GoogleMaps.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import platform.CoreGraphics.CGRectZero
import platform.CoreLocation.CLLocationCoordinate2DMake
import dev.jordond.compass.Coordinates

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun NearByMap(
    modifier: Modifier,
    coordinates: Coordinates?,
    title: String?
) {
    if (coordinates == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Unable to display map, user location not found")
        }
        return
    }

    UIKitView(
        factory = {
            val initialLocation = CLLocationCoordinate2DMake(coordinates.latitude, coordinates.longitude)
            val camera = GMSCameraPosition.cameraWithTarget(initialLocation, 12.0f)
            val mapView = GMSMapView.mapWithFrame(CGRectZero.readValue(), camera)

            mapView.settings.compassButton = true
            mapView.settings.myLocationButton = true
            mapView.myLocationEnabled = true
            mapView.settings.zoomGestures = true
            mapView.settings.scrollGestures = true
            mapView.settings.tiltGestures = true
            mapView.settings.rotateGestures = true
            mapView
        }, 
        modifier = modifier.fillMaxSize(), 
        update = { mapView -> 
            val location = CLLocationCoordinate2DMake(coordinates.latitude, coordinates.longitude)
            mapView.animateToCameraPosition(GMSCameraPosition.cameraWithTarget(location, 15.0f))
            
            mapView.clear()
            val marker = GMSMarker.markerWithPosition(location)
            marker.map = mapView
            mapView.selectedMarker = marker
            mapView.myLocationEnabled = true
        }, 
        onRelease = { }, 
        properties = UIKitInteropProperties(isInteractive = true, isNativeAccessibilityEnabled = true)
    )
}
