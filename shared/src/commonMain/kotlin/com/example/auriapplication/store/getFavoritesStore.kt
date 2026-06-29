package com.example.auriapplication.store

import com.example.auriapplication.screen.nearby.Place
import io.github.xxfast.kstore.KStore

expect fun getFavoritesStore(): KStore<List<Place>>
