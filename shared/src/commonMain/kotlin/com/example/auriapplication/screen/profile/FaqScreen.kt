package com.example.auriapplication.screen.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class FaqScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("FAQ", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Frequently Asked Questions",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                items(faqList) { faq ->
                    FaqItem(faq.question, faq.answer)
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }

    @Composable
    private fun FaqItem(question: String, answer: String) {
        var expanded by remember { mutableStateOf(false) }
        val rotation by animateFloatAsState(if (expanded) 180f else 0f)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation)
                    )
                }
                AnimatedVisibility(visible = expanded) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = answer,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    private data class FaqData(val question: String, val answer: String)

    private val faqList = listOf(
        FaqData(
            "Why do I need to enable location permissions?",
            "Auri requires location access to automatically identify holidays in your specific region and to find the best gift stores and restaurants near you. Without this, some features may be limited."
        ),
        FaqData(
            "My holiday countdown seems incorrect. How can I fix it?",
            "Countdowns are based on your device's timezone and detected location. Please ensure your device settings are set to 'Set time automatically' and that you have granted Auri location permissions."
        ),
        FaqData(
            "I can't see any nearby gift stores or restaurants. What's wrong?",
            "This can happen if you are in an area with limited data or if your GPS signal is weak. Try moving to a more open area or use the search bar in the 'Nearby' tab to manually look up a different city."
        ),
        FaqData(
            "How do I add or remove a place from my Favourites?",
            "To add a place to your favourites, simply tap the heart icon on any store card in the Home or Nearby tabs. To remove it, tap the heart icon again. You can view all your saved places by going to Profile > Favourites."
        ),
        FaqData(
            "Can I search for holidays and gifts in a different country?",
            "Currently, Auri focuses on your immediate location for holiday tracking. However, you can use the search feature in the 'Nearby' tab to explore services and gift stores in any city globally."
        ),
        FaqData(
            "The app is not updating my location. What should I do?",
            "If your location isn't updating, try restarting the app or toggling your device's Location/GPS off and on. Ensure that Auri has 'Always' or 'While Using the App' permission in your device settings."
        )
    )
}
