package com.example.auriapplication.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.auriapplication.theme.GlassGlowCard
import com.example.auriapplication.LocalUserLocation
import com.example.auriapplication.screen.nearby.LatLng
import com.example.auriapplication.screen.nearby.Place
import com.example.auriapplication.screen.nearby.PlacesRepository
import com.example.auriapplication.screen.nearby.StoreDetailsScreen
import com.example.auriapplication.screen.nearby.calculateDistance
import com.example.auriapplication.screen.nearby.formatPriceRange
import kotlin.math.roundToInt

class RestaurantListScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val userLocation = LocalUserLocation.current
        val apiKey = "AIzaSyCurf0k1ZnYq6PmqEbUrObjuT2HD0a6hL8"
        val placesRepository = remember { PlacesRepository(apiKey) }

        var allRestaurants by remember { mutableStateOf<List<Place>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var lastFetchedLocation by remember { mutableStateOf<LatLng?>(null) }

        // Filter states
        var selectedFoodType by remember { mutableStateOf("All") }
        var maxDistanceKm by remember { mutableStateOf(10.0) } // Default 10km
        var minRating by remember { mutableStateOf(0.0) }

        val foodTypes = listOf(
            "All",
            "Cafe",
            "Bakery",
            "Vegan",
            "Vegetarian",
            "Chinese",
            "Indian",
            "Italian",
            "Korean",
            "Mexican",
            "Japanese",
            "Pizza",
            "Burgers"
        )

        LaunchedEffect(userLocation.coordinates) {
            val coords = userLocation.coordinates ?: return@LaunchedEffect
            val current = LatLng(coords.latitude, coords.longitude)

            val movedDistance = lastFetchedLocation?.let { last ->
                calculateDistance(
                    current.latitude,
                    current.longitude,
                    last.latitude,
                    last.longitude
                )
            } ?: Double.MAX_VALUE

            // Prevent continuous refetching from small GPS drift.
            if (movedDistance < 500) return@LaunchedEffect

            isLoading = true
            try {
                allRestaurants =
                    placesRepository.getNearbyRestaurants(current.latitude, current.longitude)
                lastFetchedLocation = current
            } finally {
                isLoading = false
            }
        }

        val filteredRestaurants by remember(
            allRestaurants,
            selectedFoodType,
            maxDistanceKm,
            minRating,
            userLocation.coordinates
        ) {
            val coords = userLocation.coordinates
            derivedStateOf {
                allRestaurants.filter { restaurant ->
                    val matchesFoodType = matchesRestaurantFoodFilter(restaurant, selectedFoodType)
                    val matchesRating = (restaurant.rating ?: 0.0) >= minRating

                    val matchesDistance = if (coords != null && restaurant.location != null) {
                        val dist = calculateDistance(
                            coords.latitude,
                            coords.longitude,
                            restaurant.location.latitude,
                            restaurant.location.longitude
                        )
                        dist / 1000.0 <= maxDistanceKm
                    } else {
                        true
                    }

                    matchesFoodType && matchesRating && matchesDistance
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Celebrate Together") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                // Filters
                FilterSection(
                    foodTypes = foodTypes,
                    selectedFoodType = selectedFoodType,
                    onFoodTypeSelected = { selectedFoodType = it },
                    maxDistanceKm = maxDistanceKm,
                    onDistanceChange = { maxDistanceKm = it },
                    minRating = minRating,
                    onRatingChange = { minRating = it }
                )

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (filteredRestaurants.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "No restaurants found matching filters",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredRestaurants) { restaurant ->
                            RestaurantListItem(
                                restaurant = restaurant,
                                userLocation = userLocation.coordinates,
                                onClick = {
                                    restaurant.id?.let { id ->
                                        navigator.push(
                                            StoreDetailsScreen(
                                                placeId = id,
                                                initialName = restaurant.displayName?.text,
                                                initialAddress = restaurant.formattedAddress
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    foodTypes: List<String>,
    selectedFoodType: String,
    onFoodTypeSelected: (String) -> Unit,
    maxDistanceKm: Double,
    onDistanceChange: (Double) -> Unit,
    minRating: Double,
    onRatingChange: (Double) -> Unit
) {
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)) {
        Text(
            "Type of Food",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(foodTypes) { type ->
                FilterChip(
                    selected = selectedFoodType == type,
                    onClick = { onFoodTypeSelected(type) },
                    label = { Text(type) }
                )
            }
        }
    }
}

@Composable
fun RestaurantListItem(
    restaurant: Place,
    userLocation: dev.jordond.compass.Coordinates?,
    onClick: () -> Unit
) {
    val distance = remember(restaurant.location, userLocation) {
        if (restaurant.location != null && userLocation != null) {
            calculateDistance(
                userLocation.latitude, userLocation.longitude,
                restaurant.location.latitude, restaurant.location.longitude
            )
        } else null
    }

    GlassGlowCard(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
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
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = restaurant.displayName?.text ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (distance != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = formatDistance(distance),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (restaurant.rating != null) {
                    Icon(
                        Icons.Default.Star,
                        null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${restaurant.rating}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    restaurant.userRatingCount?.let {
                        Text(
                            text = " ($it)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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
                    text = restaurant.formattedAddress ?: "No address available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            val priceDisplay = formatPriceRange(restaurant.priceRange, restaurant.priceLevel)
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

            val typeLabels = restaurant.types
                .orEmpty()
                .filterNot { it in GENERIC_RESTAURANT_TYPES }
                .take(3)
                .map { it.replace("_", " ").capitalize() }

            if (typeLabels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    typeLabels.forEach { label ->
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

// Extension to avoid String.capitalize() deprecation if needed, or just use it if common
private fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

private val FOOD_TYPE_KEYWORDS = mapOf(
    "Italian" to listOf("italian_restaurant", "italian"),
    "Mexican" to listOf("mexican_restaurant", "mexican"),
    "Chinese" to listOf("chinese_restaurant", "chinese"),
    "Japanese" to listOf("japanese_restaurant", "japanese", "sushi_restaurant", "ramen_restaurant"),
    "Pizza" to listOf("pizza_restaurant", "pizza"),
    "Burgers" to listOf("hamburger_restaurant", "burger", "burgers"),
    "Cafe" to listOf("cafe", "coffee_shop", "coffee"),
    "Bakery" to listOf("bakery", "baked_goods", "cake_shop"),
    "Vegan" to listOf("vegan_restaurant", "vegan"),
    "Vegetarian" to listOf("vegetarian_restaurant", "vegetarian"),
    "Indian" to listOf("indian_restaurant", "indian"),
    "Korean" to listOf("korean_restaurant", "korean", "kimbab_restaurant", "tteokbak_restaurant")
)

private val GENERIC_RESTAURANT_TYPES =
    setOf("food", "point_of_interest", "establishment", "restaurant")

private fun matchesRestaurantFoodFilter(restaurant: Place, selectedFoodType: String): Boolean {
    if (selectedFoodType == "All") return true

    val normalizedTypes = restaurant.types.orEmpty().map { it.lowercase() }
    val normalizedName = restaurant.displayName?.text?.lowercase().orEmpty()
    val keywords = FOOD_TYPE_KEYWORDS[selectedFoodType].orEmpty()

    // 1. Strict Category Match (Check if any of the keywords exactly match or are contained in the API types)
    val hasTypeMatch = keywords.any { keyword ->
        normalizedTypes.any { type -> type == keyword || type.contains(keyword) }
    }
    if (hasTypeMatch) return true

    return keywords.any { keyword ->
        val textKeyword = keyword.replace("_", " ")
        // Ensure the keyword is not just a small substring of a random word
        normalizedName.contains(" $textKeyword ") || 
        normalizedName.startsWith("$textKeyword ") || 
        normalizedName.endsWith(" $textKeyword") ||
        normalizedName == textKeyword
    }
}

private fun formatDistance(distanceMeters: Double): String {
    return if (distanceMeters < 1000) {
        "${distanceMeters.toInt()}m"
    } else {
        val km = distanceMeters / 1000
        if (km < 10) "${(km * 10).roundToInt() / 10.0}km" else "${km.roundToInt()}km"
    }
}

