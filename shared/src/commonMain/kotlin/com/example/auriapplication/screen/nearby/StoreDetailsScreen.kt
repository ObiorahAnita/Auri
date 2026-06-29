package com.example.auriapplication.screen.nearby

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.auriapplication.theme.GlassGlowCard
import com.example.auriapplication.theme.GlassGlowText
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.decodeToImageBitmap

data class StoreDetailsScreen(
    val placeId: String,
    val initialName: String? = null,
    val initialAddress: String? = null
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val uriHandler = LocalUriHandler.current
        val apiKey = "AIzaSyCurf0k1ZnYq6PmqEbUrObjuT2HD0a6hL8"
        val repository = remember { StoreDetailsRepository(apiKey) }
        val favoritesRepository = remember { FavoritesRepository() }
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val favorites by favoritesRepository.favorites.collectAsState(initial = emptyList())
        
        var details by remember { mutableStateOf<StoreDetailsRequest?>(null) }
        var isLoading by remember { mutableStateOf(true) }

        val isFavorite = remember(favorites, placeId) { favorites.any { it.id == placeId } }

        LaunchedEffect(placeId) {
            isLoading = true
            details = repository.getStoreDetails(placeId)
            isLoading = false
        }

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { 
                        Text(
                            text = details?.displayName?.text ?: initialName ?: "Store Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    val wasFavorite = isFavorite
                                    val place = Place(
                                        id = placeId,
                                        displayName = details?.displayName ?: initialName?.let { LocalizedText(it) },
                                        formattedAddress = details?.formattedAddress ?: initialAddress,
                                        rating = details?.rating,
                                        userRatingCount = details?.userRatingCount,
                                        photos = details?.photos?.map { StorePhotoReference(it.name) }
                                    )
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
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    windowInsets = WindowInsets(0, 0, 0, 0) // Force removal of extra padding
                )
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                if (isLoading && details == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Store Photos Gallery
                        if (!details?.photos.isNullOrEmpty()) {
                            item {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(220.dp)
                                ) {
                                    items(details?.photos ?: emptyList()) { photo ->
                                        photo.name?.let { photoName ->
                                            PhotoItem(photoName = photoName, repository = repository)
                                        }
                                    }
                                }
                            }
                        }

                        // Header Info & Quick Actions
                        item {
                            GlassGlowCard(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = details?.displayName?.text ?: initialName ?: "Unknown Store",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            // Improved address resolution
                                            val addressText = details?.formattedAddress ?: initialAddress ?: details?.editorialSummary?.text ?: "No address available"
                                            InfoRow(Icons.Default.LocationOn, addressText)
                                            
                                            val priceDisplay = formatPriceRange(details?.priceRange, details?.priceLevel)
                                            if (priceDisplay != null) {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                InfoRow(Icons.Default.AttachMoney, priceDisplay)
                                            }
                                        }
                                        
                                        details?.rating?.let { rating ->
                                            Surface(
                                                color = MaterialTheme.colorScheme.secondaryContainer,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text("$rating", style = MaterialTheme.typography.labelLarge)
                                                }
                                            }
                                        }
                                    }
                                    
                                    if (details?.userRatingCount != null) {
                                        Text(
                                            text = "${details?.userRatingCount} reviews",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(start = 28.dp)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    // Main Action: Directions
                                    Button(
                                        onClick = { 
                                            details?.googleMapsUri?.let { uriHandler.openUri(it) } 
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        contentPadding = PaddingValues(12.dp)
                                    ) {
                                        Icon(Icons.Default.Directions, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Get Directions", fontWeight = FontWeight.Bold)
                                    }

                                    // Secondary Action: Website
                                    details?.websiteUri?.let { uri ->
                                        Spacer(modifier = Modifier.height(12.dp))
                                        OutlinedButton(
                                            onClick = { uriHandler.openUri(uri) },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(12.dp)
                                        ) {
                                            Icon(Icons.Default.Language, contentDescription = null)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Visit Website", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // opening Hours
                        item {
                            details?.currentOpeningHours?.let { hours ->
                                GlassGlowCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Opening Hours", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        val isOpen = hours.openNow == true
                                        GlassGlowText(
                                            text = if (isOpen) "OPEN NOW" else "CLOSED",
                                            backgroundColor = if (isOpen) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) 
                                                              else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f),
                                            textColor = if (isOpen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Black)
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        hours.weekdayDescriptions?.forEach { desc ->
                                            Text(desc, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }

                        // reviews
                        if (!details?.reviews.isNullOrEmpty()) {
                            item {
                                Text("Recent Reviews", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            }
                            items(details?.reviews ?: emptyList()) { review ->
                                GlassGlowCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(review.authorAttribution?.displayName ?: "Anonymous", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                            Spacer(modifier = Modifier.weight(1f))
                                            Text(review.relativePublishTimeDescription ?: "", style = MaterialTheme.typography.labelSmall)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            repeat(5) { index ->
                                                val rating = review.rating?.toInt() ?: 0
                                                Icon(
                                                    Icons.Default.Star, 
                                                    contentDescription = null, 
                                                    tint = if (index < rating) Color(0xFFFFB300) else Color.LightGray, 
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(review.text?.text ?: "", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun PhotoItem(photoName: String, repository: StoreDetailsRepository, modifier: Modifier = Modifier) {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var failed by remember { mutableStateOf(false) }

    LaunchedEffect(photoName) {
        isLoading = true
        failed = false
        try {
            val bytes = repository.fetchPhotoBytes(photoName)
            if (bytes != null) {
                imageBitmap = bytes.decodeToImageBitmap()
            } else {
                failed = true
            }
        } catch (e: Exception) {
            println("Failed to fetch or decode photo: $e")
            failed = true
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
    ) {
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        } else if (failed || imageBitmap == null) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = "Failed to load photo",
                    modifier = Modifier.padding(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            imageBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Store Photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

