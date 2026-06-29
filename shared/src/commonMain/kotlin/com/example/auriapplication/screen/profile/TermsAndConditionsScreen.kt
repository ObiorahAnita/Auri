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

class TermsAndConditionsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) },
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
                        text = "By using the Auri application, you agree to comply with and be bound by the following terms and conditions.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    TermsSection(
                        title = "1. Acceptance of Terms",
                        content = "By accessing and using Auri, you accept and agree to be bound by the terms and provision of this agreement. If you do not agree to these terms, please do not use the application."
                    )
                    TermsSection(
                        title = "2. Use License",
                        content = "Permission is granted to download and use the Auri mobile app for personal, non-commercial only."
                    )
                    TermsSection(
                        title = "3. User Responsibilities",
                        content = "You agree to accept responsibility for all activities that occur under your account."
                    )
                    TermsSection(
                        title = "4. Prohibited Activities",
                        content = "You agree not to use the application for any unlawful purpose or any purpose prohibited under this clause. You agree not to use the application in any way that could damage the application or general business of Auri."
                    )
                    TermsSection(
                        title = "5. Limitation of Liability",
                        content = "In no event shall Auri or its data suppliers be liable for any damages (including, without limitation, damages for loss of data or profit, or due to business interruption) arising out of the use or inability to use the application."
                    )
                    TermsSection(
                        title = "6. Governing Law",
                        content = "These terms and conditions are governed by and construed in accordance with the laws of the jurisdiction in which the company is registered, and you irrevocably submit to the exclusive jurisdiction of the courts in that State or location."
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }

    @Composable
    private fun TermsSection(title: String, content: String) {
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
