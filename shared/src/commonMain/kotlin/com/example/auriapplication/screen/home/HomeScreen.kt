package com.example.auriapplication.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.example.auriapplication.LocalUserLocation
import com.example.auriapplication.screen.nearby.FavoritesRepository
import com.example.auriapplication.screen.nearby.Place
import com.example.auriapplication.screen.nearby.PlacesRepository
import com.example.auriapplication.screen.nearby.StoreDetailsScreen
import com.example.auriapplication.screen.nearby.StoreDetailsRepository
import com.example.auriapplication.screen.nearby.calculateDistance
import com.example.auriapplication.screen.nearby.formatPriceRange
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Store
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource
import auriapplication.shared.generated.resources.Res
import auriapplication.shared.generated.resources.auri_logo_nobg
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Restaurant
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.example.auriapplication.tab.chatbot.ChatBotTab

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val tabNavigator = LocalTabNavigator.current
        val userLocation = LocalUserLocation.current
        val apiKey = "AIzaSyCurf0k1ZnYq6PmqEbUrObjuT2HD0a6hL8"
        
        val httpClient = remember {
            HttpClient {
                install(ContentNegotiation) {
                    json(Json { ignoreUnknownKeys = true })
                }
            }
        }
        val holidayRepository = remember { HolidayRepository(httpClient) }
        val calendarProvider = remember { CalendarProvider() }
        val countdownRepository = remember { 
            EventCountdownRepository(holidayRepository, calendarProvider) 
        }
        val placesRepository = remember { PlacesRepository(apiKey) }
        val favoritesRepository = remember { FavoritesRepository() }
        val snackbarHostState = remember { SnackbarHostState() }
        val detailsRepository = remember { StoreDetailsRepository(apiKey) }
        
        var bannerState by remember { mutableStateOf(GiftBannerUiState()) }
        var trendingStores by remember { mutableStateOf<List<Place>>(emptyList()) }
        var nearbyRestaurants by remember { mutableStateOf<List<Place>>(emptyList()) }
        var funActivities by remember { mutableStateOf<List<Place>>(emptyList()) }
        var studySpots by remember { mutableStateOf<List<Place>>(emptyList()) }
        
        var isLoadingTrending by remember { mutableStateOf(true) }
        var isLoadingRestaurants by remember { mutableStateOf(true) }
        var isLoadingActivities by remember { mutableStateOf(true) }
        var isLoadingStudySpots by remember { mutableStateOf(true) }
        
        var lastFetchedLocation by remember { mutableStateOf<com.example.auriapplication.screen.nearby.LatLng?>(null) }

        // 1. Fetch Banner State - Triggered by country code changes
        LaunchedEffect(userLocation.countryCode) {
            val countryCode = if (!userLocation.countryCode.isNullOrBlank()) userLocation.countryCode.uppercase() else "US"
            bannerState = countdownRepository.getUpcomingEventBannerState(countryCode)
        }

        // 2. Fetch Discovery Content - Triggered by significant location changes
        LaunchedEffect(userLocation.coordinates) {
            userLocation.coordinates?.let { coords ->
                val currentLatLng = com.example.auriapplication.screen.nearby.LatLng(coords.latitude, coords.longitude)
                val distance = lastFetchedLocation?.let { last ->
                    calculateDistance(currentLatLng.latitude, currentLatLng.longitude, last.latitude, last.longitude)
                } ?: Double.MAX_VALUE

                if (distance > 500) {
                    isLoadingTrending = true
                    isLoadingRestaurants = true
                    isLoadingActivities = true
                    isLoadingStudySpots = true
                    
                    val rawTrending = placesRepository.getNearbyPopularStores(coords.latitude, coords.longitude)
                    trendingStores = rawTrending.sortedBy { store ->
                        val storeLoc = store.location ?: return@sortedBy Double.MAX_VALUE
                        calculateDistance(coords.latitude, coords.longitude, storeLoc.latitude, storeLoc.longitude)
                    }
                    isLoadingTrending = false
                    
                    val rawRestaurants = placesRepository.getNearbyRestaurants(coords.latitude, coords.longitude)
                    nearbyRestaurants = rawRestaurants.sortedBy { restaurant ->
                        val loc = restaurant.location ?: return@sortedBy Double.MAX_VALUE
                        calculateDistance(coords.latitude, coords.longitude, loc.latitude, loc.longitude)
                    }
                    isLoadingRestaurants = false
                    
                    val rawActivities = placesRepository.getFunActivitiesNearby(coords.latitude, coords.longitude)
                    funActivities = rawActivities.sortedBy { activity ->
                        val loc = activity.location ?: return@sortedBy Double.MAX_VALUE
                        calculateDistance(coords.latitude, coords.longitude, loc.latitude, loc.longitude)
                    }
                    isLoadingActivities = false

                    val rawStudySpots = placesRepository.getStudentStudySpots(coords.latitude, coords.longitude)
                    studySpots = rawStudySpots.sortedBy { spot ->
                        val loc = spot.location ?: return@sortedBy Double.MAX_VALUE
                        calculateDistance(coords.latitude, coords.longitude, loc.latitude, loc.longitude)
                    }
                    isLoadingStudySpots = false
                    
                    lastFetchedLocation = currentLatLng
                }
            }
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                // 0. App Logo Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 0.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.auri_logo_nobg),
                            contentDescription = "Auri Logo",
                            modifier = Modifier
                                .height(150.dp)
                        )

                        IconButton(
                            onClick = { navigator.push(NotificationScreen()) },
                            modifier = Modifier.padding(top = 32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                }

                // 1. Countdown Banner
                if (bannerState.isVisible) {
                    item {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp)) {
                            GiftCountdownBanner(bannerState)
                        }
                    }
                }

                // 2. Find the Perfect Gift CTA
                item {
                    FindGiftCTA(onClick = { tabNavigator.current = ChatBotTab })
                }

                // 3. Trending Stores Section
                item {
                    SectionHeader("Popular Stores Near You", modifier = Modifier.padding(top = 16.dp))
                }

                item {
                    if (isLoadingTrending) {
                        LoadingShimmer()
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(trendingStores) { store ->
                                StoreDiscoveryCard(
                                    store = store,
                                    detailsRepository = detailsRepository,
                                    favoritesRepository = favoritesRepository,
                                    snackbarHostState = snackbarHostState,
                                    onClick = {
                                        store.id?.let { id ->
                                            navigator.push(StoreDetailsScreen(placeId = id, initialName = store.displayName?.text))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // 4. Nearby Restaurants Section
                if (!isLoadingRestaurants && nearbyRestaurants.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navigator.push(RestaurantListScreen()) }
                                .padding(top = 24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SectionHeader("Celebrate Together", modifier = Modifier.weight(1f))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "View All",
                                modifier = Modifier.padding(end = 16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(nearbyRestaurants) { restaurant ->
                                StoreDiscoveryCard(
                                    store = restaurant,
                                    detailsRepository = detailsRepository,
                                    favoritesRepository = favoritesRepository,
                                    snackbarHostState = snackbarHostState,
                                    isRestaurant = true,
                                    onClick = {
                                        restaurant.id?.let { id ->
                                            navigator.push(
                                                StoreDetailsScreen(
                                                    placeId = id,
                                                    initialName = restaurant.displayName?.text
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // 5. Things to Do Section
                if (!isLoadingActivities && funActivities.isNotEmpty()) {
                    item {
                        SectionHeader("Things to Do Nearby", modifier = Modifier.padding(top = 24.dp))
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(funActivities) { activity ->
                                StoreDiscoveryCard(
                                    store = activity,
                                    detailsRepository = detailsRepository,
                                    favoritesRepository = favoritesRepository,
                                    snackbarHostState = snackbarHostState,
                                    onClick = {
                                        activity.id?.let { id ->
                                            navigator.push(
                                                StoreDetailsScreen(
                                                    placeId = id,
                                                    initialName = activity.displayName?.text
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                if (!isLoadingStudySpots && studySpots.isNotEmpty()) {
                    item {
                        SectionHeader("Student Study Spots Nearby", modifier = Modifier.padding(top = 24.dp))
                    }

                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(studySpots) { spot ->
                                StoreDiscoveryCard(
                                    store = spot,
                                    detailsRepository = detailsRepository,
                                    favoritesRepository = favoritesRepository,
                                    snackbarHostState = snackbarHostState,
                                    onClick = {
                                        spot.id?.let { id ->
                                            navigator.push(
                                                StoreDetailsScreen(
                                                    placeId = id,
                                                    initialName = spot.displayName?.text
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
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun FindGiftCTA(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Find the perfect gift",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Let Auri Genie AI help you generate a special gift idea",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun LoadingShimmer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(32.dp))
    }
}

@Composable
fun GiftCountdownBanner(state: GiftBannerUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(56.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (state.isPersonal) Icons.Default.CalendarMonth else Icons.Default.CardGiftcard,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = state.eventName.uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = when {
                            state.daysUntilEvent == 0 -> "TODAY!"
                            state.daysUntilEvent == 1 -> "TOMORROW!"
                            else -> "${state.daysUntilEvent} DAYS LEFT"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun StoreDiscoveryCard(
    store: Place,
    detailsRepository: StoreDetailsRepository,
    favoritesRepository: FavoritesRepository,
    snackbarHostState: SnackbarHostState,
    isRestaurant: Boolean = false,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val favorites by favoritesRepository.favorites.collectAsState(initial = emptyList())
    val isFavorite = remember(favorites, store.id) { favorites.any { it.id == store.id } }

    val photoName = store.photos?.firstOrNull()?.name
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    
    LaunchedEffect(photoName) {
        if (photoName != null) {
            val bytes = detailsRepository.fetchPhotoBytes(photoName)
            if (bytes != null) {
                try {
                    imageBitmap = bytes.decodeToImageBitmap()
                } catch (e: Exception) {}
            }
        }
    }

    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!,
                        contentDescription = store.displayName?.text,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        if (isRestaurant) Icons.Default.Restaurant else Icons.Default.Store,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center).size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
                
                store.rating?.let { rating ->
                    Surface(
                        modifier = Modifier.padding(8.dp).align(Alignment.TopEnd),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("$rating", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                IconButton(
                    onClick = {
                        scope.launch {
                            val wasFavorite = isFavorite
                            favoritesRepository.toggleFavorite(store)
                            if (!wasFavorite) {
                                snackbarHostState.showSnackbar("Added to favorites")
                            } else {
                                snackbarHostState.showSnackbar("Removed from favorites")
                            }
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = store.displayName?.text ?: "Unknown",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                val type = store.types?.firstOrNull()?.replace("_", " ")?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() } ?: "Place"
                val priceDisplay = formatPriceRange(store.priceRange, store.priceLevel)
                Text(
                    text = if (priceDisplay != null) "$type • $priceDisplay" else type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
