package com.example.auriapplication

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.example.auriapplication.platform.rememberGeocoder
import com.example.auriapplication.platform.rememberGeolocator
import com.example.auriapplication.store.getThemeStore
import com.example.auriapplication.tab.home.HomeTab
import com.example.auriapplication.tab.nearby.NearByTab
import com.example.auriapplication.tab.chatbot.ChatBotTab
import com.example.auriapplication.tab.profile.ProfileTab
import com.example.auriapplication.theme.AuriApplicationTheme
import com.example.auriapplication.theme.LocalThemeIsDark
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.TrackingStatus
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geocoder.placeOrNull
import kotlin.math.abs

data class UserLocationState(
    val coordinates: Coordinates? = null,
    val cityName: String? = null,
    val countryCode: String? = null
)

val LocalUserLocation = compositionLocalOf<UserLocationState> { UserLocationState() }

@Composable
@Preview
fun App() {
    val systemDark = isSystemInDarkTheme()
    val isDark = remember { mutableStateOf<Boolean>(systemDark) }
    val themeStore = remember { getThemeStore() }

    LaunchedEffect(Unit) {
        themeStore.get()?.let { savedIsDark ->
            isDark.value = savedIsDark
        }
    }

    LaunchedEffect(isDark.value) {
        themeStore.set(isDark.value)
    }

    CompositionLocalProvider(LocalThemeIsDark provides isDark) {
        AuriApplicationTheme {
            val geolocator = rememberGeolocator()
            val geocoder = rememberGeocoder()
            val trackingStatus by geolocator.trackingStatus.collectAsState(TrackingStatus.Idle)
            var userLocationState by remember { mutableStateOf(UserLocationState()) }

            LaunchedEffect(trackingStatus) {
                when (val status = trackingStatus) {
                    is TrackingStatus.Update -> {
                        val coords = status.location.coordinates
                        val lastCoords = userLocationState.coordinates

                        // Only geocode if this is the first location or if the user has moved significantly (~500m)
                        val shouldGeocode = lastCoords == null ||
                                abs(coords.latitude - lastCoords.latitude) > 0.005 ||
                                abs(coords.longitude - lastCoords.longitude) > 0.005

                        if (shouldGeocode) {
                            val place = geocoder.placeOrNull(coords)
                            val cityName = place?.locality
                            val countryCode = place?.isoCountryCode
                            userLocationState = UserLocationState(coords, cityName, countryCode)
                        } else {
                            userLocationState = userLocationState.copy(coordinates = coords)
                        }
                    }

                    is TrackingStatus.Error -> {
                        val result = status.cause
                        when (result) {
                            is GeolocatorResult.NotSupported -> println("LOCATION ERROR: ${result.message}")
                            is GeolocatorResult.NotFound -> println("LOCATION ERROR: ${result.message}")
                            is GeolocatorResult.PermissionDenied -> println("LOCATION ERROR: ${result.message}")
                            is GeolocatorResult.GeolocationFailed -> println("LOCATION ERROR: ${result.message}")
                            else -> println("LOCATION ERROR: Unknown error")
                        }
                    }

                    else -> {}
                }
            }

            LaunchedEffect(Unit) {
                geolocator.track(LocationRequest(Priority.HighAccuracy)).collect {
                    // Tracking is active while this flow is collected
                }
            }


            CompositionLocalProvider(LocalUserLocation provides userLocationState) {
                TabNavigator(HomeTab) {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                TabNavigationItem(HomeTab)
                                TabNavigationItem(NearByTab)
                                TabNavigationItem(ChatBotTab)
                                TabNavigationItem(ProfileTab)
                            }
                        }
                    ) { paddingValues ->
                        Box(Modifier.padding(paddingValues)) {
                            CurrentTab()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current
    NavigationBarItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        label = { Text(tab.options.title) },
        icon = {
            tab.options.icon?.let { icon ->
                Icon(painter = icon, contentDescription = tab.options.title)
            }
        }
    )
}
