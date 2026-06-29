package com.example.auriapplication.screen.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class PrivacyPolicyScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
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
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Last Updated: June 2026",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your privacy is important to us. This Privacy Policy explains how Auri collects, uses, and protects your information.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    PolicySection(
                        title = "1. Information We Collect",
                        content = "We collect information you provide directly to us, such as when you create an account or interact with the ChatBot. We also collect location data to provide regional holiday information and nearby discovery services."
                    )
                    PolicySection(
                        title = "2. Use of Location Data",
                        content = "Auri requires access to your device's location to function effectively. This data is used solely to identify local holidays and find nearby gift stores and services. We do not sell your location history to third parties."
                    )
                    PolicySection(
                        title = "3. Third-Party Services",
                        content = "We use third-party APIs, such as Google Maps API, to provide mapping and location-based search results. These services may collect information as described in their own privacy policies."
                    )
                    PolicySection(
                        title = "4. Data Security",
                        content = "We implement industry-standard security measures to protect your data."
                    )
                    PolicySection(
                        title = "5. Your Rights",
                        content = "Depending on your location, you may have rights regarding your personal data, including the right to access, correct, or delete the information we have about you."
                    )
                    PolicySection(
                        title = "6. Changes to This Policy",
                        content = "We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page."
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }

    @Composable
    private fun PolicySection(title: String, content: String) {
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
