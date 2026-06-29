package com.example.auriapplication.screen.nearby

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.auriapplication.theme.GlassGlowCard
import com.example.auriapplication.LocalUserLocation
import com.example.auriapplication.platform.NearByMap
import com.example.auriapplication.platform.rememberAutocomplete
import com.example.auriapplication.screen.nearby.Place as NearbyPlace
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.AutocompleteResult
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NearByScreen : Screen {
    enum class ViewState {
        MAP_ONLY,
        SPLIT,
        LIST_ONLY
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val userLocationState = LocalUserLocation.current
        var searchText by remember { mutableStateOf("") }
        val autocomplete = rememberAutocomplete()
        var suggestions by remember { mutableStateOf<List<Place>>(emptyList()) }
        
        var selectedCoordinates by remember { mutableStateOf<Coordinates?>(null) }
        var selectedCityName by remember { mutableStateOf<String?>(null) }

        var viewState by remember { mutableStateOf(ViewState.SPLIT) }
        
        val mapHeight by animateFloatAsState(
            targetValue = when (viewState) {
                ViewState.MAP_ONLY -> 1f
                ViewState.SPLIT -> 0.45f
                ViewState.LIST_ONLY -> 0f
            },
            label = "MapFractionAnimation"
        )

        val repository = remember { PlacesRepository("AIzaSyCurf0k1ZnYq6PmqEbUrObjuT2HD0a6hL8") }
        val favoritesRepository = remember { FavoritesRepository() }
        val snackbarHostState = remember { SnackbarHostState() }
        var nearbyPlaces by remember { mutableStateOf<List<NearbyPlace>>(emptyList()) }
        var isLoadingPlaces by remember { mutableStateOf(false) }
        var lastFetchedLocation by remember { mutableStateOf<Coordinates?>(null) }

        LaunchedEffect(selectedCoordinates ?: userLocationState.coordinates) {
            val currentCoords = selectedCoordinates ?: userLocationState.coordinates ?: return@LaunchedEffect
            
            if (selectedCoordinates == null) {
                lastFetchedLocation?.let { last ->
                    val distance = calculateDistance(
                        currentCoords.latitude, currentCoords.longitude,
                        last.latitude, last.longitude
                    )
                    if (distance < 500) return@LaunchedEffect
                }
            }
            
            // Optimization: Skip delay for explicit searches, use minimal delay for location updates
            isLoadingPlaces = true
            if (selectedCoordinates == null) delay(300) 
            
            try {
                val fetchedPlaces = repository.getNearbyGiftStores(currentCoords.latitude, currentCoords.longitude)
                // Sort the results so the closest ones are at the top
                nearbyPlaces = fetchedPlaces.sortedBy { place ->
                    val placeLoc = place.location ?: return@sortedBy Double.MAX_VALUE
                    calculateDistance(
                        currentCoords.latitude, currentCoords.longitude,
                        placeLoc.latitude, placeLoc.longitude
                    )
                }
                lastFetchedLocation = currentCoords
            } catch (e: Exception) {
                // Silently handle error
            } finally {
                isLoadingPlaces = false
            }
        }


        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()

        LaunchedEffect(searchText) {
            if (searchText.length >= 3) {
                delay(500) // Debounce
                val result = autocomplete.search(searchText)
                if (result is AutocompleteResult.Success) {
                    suggestions = result.data
                }
            } else {
                suggestions = emptyList()
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        LocationSearchBar(
                            query = searchText,
                            onQueryChange = { searchText = it },
                            onClearQuery = {
                                searchText = ""
                                suggestions = emptyList()
                                selectedCoordinates = null
                                selectedCityName = null
                            },
                            onSearch = {
                                scope.launch {
                                    val result = autocomplete.search(searchText)
                                    if (result is AutocompleteResult.Success && result.data.isNotEmpty()) {
                                        val place = result.data.first()
                                        selectedCoordinates = place.coordinates
                                        selectedCityName = place.locality ?: place.name
                                        searchText = place.name ?: searchText
                                        suggestions = emptyList()
                                        focusManager.clearFocus()
                                    }
                                }
                            }
                        )
                    }
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (viewState != ViewState.LIST_ONLY) {
                        Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(mapHeight)) {
                            NearByMap(
                                modifier = Modifier.fillMaxSize(),
                                coordinates = selectedCoordinates ?: userLocationState.coordinates,
                                title = selectedCityName ?: userLocationState.cityName
                            )

                            FloatingActionButton(
                                onClick = {
                                    viewState = if (viewState == ViewState.MAP_ONLY) ViewState.SPLIT else ViewState.MAP_ONLY
                                },
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = if (viewState == ViewState.MAP_ONLY) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = if (viewState == ViewState.MAP_ONLY) "Minimize Map" else "Expand Map"
                                )
                            }
                        }
                    }

                    NearbyStoresList(
                        places = nearbyPlaces,
                        isLoading = isLoadingPlaces,
                        userLocation = selectedCoordinates ?: userLocationState.coordinates,
                        isExpanded = viewState == ViewState.LIST_ONLY,
                        favoritesRepository = favoritesRepository,
                        snackbarHostState = snackbarHostState,
                        onToggleExpand = {
                            viewState = if (viewState == ViewState.LIST_ONLY) ViewState.SPLIT else ViewState.LIST_ONLY
                        },
                        onStoreClick = { place ->
                            place.id?.let { id ->
                                navigator.push(StoreDetailsScreen(
                                    placeId = id,
                                    initialName = place.displayName?.text,
                                    initialAddress = place.formattedAddress
                                ))
                            }
                        },
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    )
                }

                if (suggestions.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .heightIn(max = 300.dp)
                            .zIndex(1f),
                        shape = RoundedCornerShape(12.dp),
                        tonalElevation = 8.dp,
                        shadowElevation = 8.dp
                    ) {
                        LazyColumn {
                            items(suggestions) { place ->
                                val name = place.name ?: ""
                                ListItem(
                                    headlineContent = { Text(name) },
                                    supportingContent = { Text("${place.locality ?: ""}, ${place.country ?: ""}") },
                                    modifier = Modifier.clickable {
                                        selectedCoordinates = place.coordinates
                                        selectedCityName = place.locality ?: name
                                        searchText = name
                                        suggestions = emptyList()
                                        focusManager.clearFocus()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationSearchBar(
    query: String, 
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onSearch: () -> Unit
){
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("Search city or address", style = MaterialTheme.typography.bodyLarge) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearQuery) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            }
        },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        singleLine = true
    )
}

@Composable
fun NearbyStoresList(
    places: List<NearbyPlace>,
    isLoading: Boolean,
    userLocation: Coordinates?,
    isExpanded: Boolean,
    favoritesRepository: FavoritesRepository,
    snackbarHostState: SnackbarHostState,
    onToggleExpand: () -> Unit,
    onStoreClick: (NearbyPlace) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNoResults by remember { mutableStateOf(false) }

    LaunchedEffect(isLoading, places) {
        if (isLoading) {
            showNoResults = false
        } else if (places.isEmpty()) {
            delay(2000) 
            showNoResults = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isExpanded) {
                detectVerticalDragGestures { change, dragAmount ->
                    // Drag up to expand
                    if (dragAmount < -20 && !isExpanded) {
                        onToggleExpand()
                        change.consume()
                    }
                    // Drag down to minimize
                    else if (dragAmount > 20 && isExpanded) {
                        onToggleExpand()
                        change.consume()
                    }
                }
            }
    ) {
        // Sliding Toggle Handle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clickable { onToggleExpand() },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.size(width = 40.dp, height = 4.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(2.dp)
            ) {}
        }

        if (isLoading || (places.isEmpty() && !showNoResults)) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Finding stores near you...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        } else if (places.isEmpty() && showNoResults) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No stores found nearby",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Stores Nearby",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                items(places) { place ->
                    StoreItem(
                        place = place,
                        userLocation = userLocation,
                        favoritesRepository = favoritesRepository,
                        snackbarHostState = snackbarHostState,
                        onClick = { onStoreClick(place) }
                    )
                }
            }
        }
    }
}

@Composable
fun StoreItem(
    place: NearbyPlace,
    userLocation: Coordinates?,
    favoritesRepository: FavoritesRepository,
    snackbarHostState: SnackbarHostState,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val favorites by favoritesRepository.favorites.collectAsState(initial = emptyList())
    val isFavorite = remember(favorites, place.id) { favorites.any { it.id == place.id } }

    val distance = remember(place.location, userLocation) {
        if (place.location != null && userLocation != null) {
            calculateDistance(
                userLocation.latitude, userLocation.longitude,
                place.location.latitude, place.location.longitude
            )
        } else null
    }

    GlassGlowCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = place.displayName?.text ?: "Unknown Store",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (place.rating != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${place.rating}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (place.userRatingCount != null) {
                            Text(
                                text = " (${place.userRatingCount})",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                if (distance != null) {
                    val distanceText = if (distance < 1000) {
                        "${distance.toInt()}m"
                    } else {
                        val km = distance / 1000
                        if (km < 10) "${(km * 10).roundToInt() / 10.0}km"
                        else "${km.roundToInt()}km"
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = distanceText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                IconButton(
                    onClick = {
                        scope.launch {
                            val wasFavorite = isFavorite
                            favoritesRepository.toggleFavorite(place)
                            if (!wasFavorite) {
                                snackbarHostState.showSnackbar("Added to favorites")
                            } else {
                                snackbarHostState.showSnackbar("Removed from favorites")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = place.formattedAddress ?: "No address available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            val priceDisplay = formatPriceRange(place.priceRange, place.priceLevel)
            if (priceDisplay != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = priceDisplay,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!place.types.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    place.types.take(3).forEach { type ->
                        val label = type.replace("_", " ")
                            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                        
                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
        }
    }
}

