package com.example.auriapplication.store

import com.example.auriapplication.screen.home.GiftEvent
import io.github.xxfast.kstore.KStore

expect fun getEventStore(): KStore<List<GiftEvent>>
