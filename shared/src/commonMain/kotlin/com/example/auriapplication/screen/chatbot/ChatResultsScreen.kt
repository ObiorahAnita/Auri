package com.example.auriapplication.screen.chatbot

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.ktor.client.HttpClient
import com.example.auriapplication.util.getEpochMillis
import kotlinx.coroutines.launch

class ChatResultsScreen(
    private val initialPrompt: String,
    private val initialResponse: String,
    private val httpClient: HttpClient,
    private val backendUrl: String = "http://localhost:8081"
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val focusManager = LocalFocusManager.current
        val scope = rememberCoroutineScope()
        val apiClient = remember { com.example.auriapplication.network.AuriApiClient(httpClient, backendUrl) }

        val chatMessages = remember { 
            val now = getEpochMillis()
            mutableStateListOf(
                ChatMessage(initialPrompt, isUser = true, timestamp = now),
                ChatMessage(initialResponse, isUser = false, timestamp = now)
            )
        }
        
        var nextInput by remember { mutableStateOf("") }
        var isSending by remember { mutableStateOf(false) }
        val listState = rememberLazyListState()

        LaunchedEffect(chatMessages.size) {
            if (chatMessages.isNotEmpty()) {
                listState.animateScrollToItem(chatMessages.size - 1)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Auri Assistant", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    }
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chatMessages) { message ->
                        ChatBubble(message)
                    }
                    if (isSending) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            }
                        }
                    }
                }

                Surface(
                    tonalElevation = 2.dp,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = nextInput,
                            onValueChange = { nextInput = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Ask follow-up...") },
                            maxLines = 3,
                            enabled = !isSending,
                            shape = RoundedCornerShape(24.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (nextInput.isNotBlank()) {
                                    scope.launch {
                                        val prompt = nextInput
                                        nextInput = ""
                                        isSending = true
                                        val now = getEpochMillis()
                                        chatMessages.add(ChatMessage(prompt, isUser = true, timestamp = now))
                                        
                                        val result = apiClient.getChatResponse(prompt)
                                        result.onSuccess {
                                            // The backend returns ChatResponse(response: String)
                                            chatMessages.add(ChatMessage(it.response, isUser = false, timestamp = getEpochMillis()))
                                        }.onFailure {
                                            chatMessages.add(ChatMessage("Sorry, I encountered an error: ${it.message}", isUser = false, timestamp = getEpochMillis()))
                                        }
                                        isSending = false
                                        focusManager.clearFocus()
                                    }
                                }
                            })
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (nextInput.isNotBlank()) {
                                    scope.launch {
                                        val prompt = nextInput
                                        nextInput = ""
                                        isSending = true
                                        val now = getEpochMillis()
                                        chatMessages.add(ChatMessage(prompt, isUser = true, timestamp = now))
                                        
                                        val result = apiClient.getChatResponse(prompt)
                                        result.onSuccess {
                                            chatMessages.add(ChatMessage(it.response, isUser = false, timestamp = getEpochMillis()))
                                        }.onFailure {
                                            chatMessages.add(ChatMessage("Sorry, I encountered an error: ${it.message}", isUser = false, timestamp = getEpochMillis()))
                                        }
                                        isSending = false
                                        focusManager.clearFocus()
                                    }
                                }
                            },
                            enabled = nextInput.isNotBlank() && !isSending,
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ChatBubble(message: ChatMessage) {
        val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
        val bgColor = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
        val textColor = if (message.isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        val shape = if (message.isUser) {
            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
        } else {
            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
            Surface(
                color = bgColor,
                shape = shape,
                tonalElevation = 1.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp)
                )
            }
        }
    }
}
