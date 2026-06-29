# AuriApplication - Shared Module

This is the core shared module of the **AuriApplication**, built using **Kotlin Multiplatform (KMP)**. It contains the shared business logic, data models, networking, and UI components used across Android, iOS, and other supported platforms.
Only the Android and iOS devices were implemented.

## 🚀 Overview

AuriApplication is a multi-purpose discovery and event tracking app designed to help users find perfect gifts, discover local spots, and celebrate special moments.

### Key Features
- **Event Countdown**: Track upcoming holidays and personal events with a dynamic countdown banner.
- **Nearby Discovery**: Find popular stores, restaurants, study spots, and fun activities based on your real-time location.
- **Global Search**: Map View allows users to see stores nearby or search for any city in the world to discover local businesses globally.
- **Advanced Restaurant Filtering**: Search for specific cuisines (Korean, Italian, Indian, etc.) with strict, category-aware filtering.
- **Detailed Store Insights**: View photos, reviews, and opening hours for local businesses.
    - **Get Directions**: Redirects to Google Maps for navigation from the user's location.
    - **Visit Website**: Direct access to the official website for additional store information.
- **Favorites**: Save your favorite spots for quick access.
- **Auri Genie AI**: An integrated ChatBot to help generate gift ideas and provide assistance (The integration was not completed, the chatbot doesn't work for now).
- **Location Services**: Real-time geolocation and geocoding support.

## 🛠 Core Frameworks & Libraries

- **UI Framework**: [Compose Multiplatform](https://github.com/JetBrains/compose-multiplatform)
- **Navigation**: [Voyager](https://voyager.adriel.cafe/) (Multiplatform navigation library)
- **Networking**: [Ktor](https://ktor.io/) (Asynchronous HTTP client)
- **Serialization**: [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization)
- **Local Storage**: [KStore](https://github.com/xxfast/kstore) (Tiny Kotlin Multiplatform library for storage)
- **Location & Maps**: 
    - [Compass](https://github.com/jordond/compass) (KMP Geolocation and Geocoding)
    - Google Places API
    - Google Maps (Platform-specific implementations)
- **Concurrency**: Kotlin Coroutines
- **Date & Time**: Kotlinx DateTime
- **Dependency Management**: Gradle Version Catalogs

## 📂 Project Structure (Shared Module)

```
shared/src/commonMain/kotlin/com/example/auriapplication/
├── network/    # Ktor API client and network configuration
├── platform/   # Expect/Actual declarations for platform-specific logic
├── screen/     # UI Screens (Home, Nearby, ChatBot, Profile, etc.)
├── store/      # Persistence logic using KStore
├── tab/        # Tab definitions for the main navigation bar
├── theme/      # Shared UI theme (Colors, Typography, GlassGlow effects)
├── util/       # Common utilities
└── App.kt      # Main application entry point & TabNavigator setup
```

## 🔑 Setup & Configuration

### API Keys
The project uses the **Google Maps Platform API**. Currently, the key is referenced in:
- `PlacesRepository.kt`
- `StoreDetailsRepository.kt`
- `NearByScreen.kt`
- `HomeScreen.kt`

### Platform Specifics
- **Android**: Requires fine/coarse location and calendar permissions.
- **iOS**: Uses CocoaPods for dependency management (including Google Maps).

## 🔨 Build Instructions

To build the shared module:
```bash
./gradlew :shared:assemble
```

To run tests:
```bash
./gradlew :shared:allTests
```
