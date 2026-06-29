package com.example.auriapplication.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object LightThemeColors {
    val primary = Color(0xFFF2BED7)           // Blush Pink
    val onPrimary = Color(0xFFFFFFFF)         // White
    val primaryContainer = Color(0xFFFAFAFA)  // Opaque White
    val onPrimaryContainer = Color(0xFF454746) // Dark Grey

    val secondary = Color(0xFF06B6D4)         // Cyan
    val onSecondary = Color(0xFFFFFFFF)       // White
    val secondaryContainer = Color(0xFFCFFAFE) // Light Cyan
    val onSecondaryContainer = Color(0xFF087E8B) // Dark Cyan

    val tertiary = Color(0xFF8B5CF6)          // Purple
    val onTertiary = Color(0xFFFFFFFF)        // White
    val tertiaryContainer = Color(0xFFF3E8FF) // Light Purple
    val onTertiaryContainer = Color(0xFF5B21B6) // Dark Purple

    val background = Color(0xFFFAFAFA)        // Off-white
    val onBackground = Color(0xFF1A1A1A)      // Near black

    val surface = Color(0xFFFEFEFE)           // Pure white with slight transparency
    val onSurface = Color(0xFF1C1C1C)         // Charcoal
    val surfaceVariant = Color(0xFFEEEEEE)    // Light gray
    val onSurfaceVariant = Color(0xFF4A4A4A)  // Medium gray

    val error = Color(0xFFD32F2F)             // Red
    val onError = Color(0xFFFFFFFF)           // White
    val errorContainer = Color(0xFFFFEBEE)    // Light red
    val onErrorContainer = Color(0xFF7F1B1B) // Dark red

    val outline = Color(0xFFCCCCCC)           // Light outline
    val outlineVariant = Color(0xFFDDDDDD)    // Lighter outline
}

object DarkThemeColors {
    val primary = Color(0xFFF2BED7)           // Light Indigo
    val onPrimary = Color(0xFF1E1E2E)         // Very dark background
    val primaryContainer = Color(0xFF005A72)  // Dark purple-indigo
    val onPrimaryContainer = Color(0xFFE0E7FF) // Light text

    val secondary = Color(0xFF67E8F9)         // Bright Cyan
    val onSecondary = Color(0xFF0F2F3A)       // Very dark
    val secondaryContainer = Color(0xFF005A72) // Dark cyan
    val onSecondaryContainer = Color(0xFF67E8F9) // Bright cyan text

    val tertiary = Color(0xFFC4B5FD)          // Light Purple
    val onTertiary = Color(0xFF2E1065)        // Dark purple background
    val tertiaryContainer = Color(0xFF581C87) // Dark purple
    val onTertiaryContainer = Color(0xFFF3E8FF) // Light text

    val background = Color(0xFF0F0F1A)        // Very dark navy
    val onBackground = Color(0xFFF5F5F5)      // Light text

    val surface = Color(0xFF1A1A2E)           // Dark surface
    val onSurface = Color(0xFFE8E8E8)         // Light text
    val surfaceVariant = Color(0xFF2D2D44)    // Slightly lighter surface
    val onSurfaceVariant = Color(0xFFB0B0B0)  // Medium light gray

    val error = Color(0xFFEF5350)             // Light red
    val onError = Color(0xFF5D1A1A)           // Dark red background
    val errorContainer = Color(0xFF78261E)    // Dark red container
    val onErrorContainer = Color(0xFFFFEBEE) // Light text

    val outline = Color(0xFF545454)           // Dark outline
    val outlineVariant = Color(0xFF3F3F3F)    // Darker outline
}

// Create light color scheme
fun createLightColorScheme() = lightColorScheme(
    primary = LightThemeColors.primary,
    onPrimary = LightThemeColors.onPrimary,
    primaryContainer = LightThemeColors.primaryContainer,
    onPrimaryContainer = LightThemeColors.onPrimaryContainer,
    secondary = LightThemeColors.secondary,
    onSecondary = LightThemeColors.onSecondary,
    secondaryContainer = LightThemeColors.secondaryContainer,
    onSecondaryContainer = LightThemeColors.onSecondaryContainer,
    tertiary = LightThemeColors.tertiary,
    onTertiary = LightThemeColors.onTertiary,
    tertiaryContainer = LightThemeColors.tertiaryContainer,
    onTertiaryContainer = LightThemeColors.onTertiaryContainer,
    background = LightThemeColors.background,
    onBackground = LightThemeColors.onBackground,
    surface = LightThemeColors.surface,
    onSurface = LightThemeColors.onSurface,
    surfaceVariant = LightThemeColors.surfaceVariant,
    onSurfaceVariant = LightThemeColors.onSurfaceVariant,
    error = LightThemeColors.error,
    onError = LightThemeColors.onError,
    errorContainer = LightThemeColors.errorContainer,
    onErrorContainer = LightThemeColors.onErrorContainer,
    outline = LightThemeColors.outline,
    outlineVariant = LightThemeColors.outlineVariant,
)

// Create dark color scheme
fun createDarkColorScheme() = darkColorScheme(
    primary = DarkThemeColors.primary,
    onPrimary = DarkThemeColors.onPrimary,
    primaryContainer = DarkThemeColors.primaryContainer,
    onPrimaryContainer = DarkThemeColors.onPrimaryContainer,
    secondary = DarkThemeColors.secondary,
    onSecondary = DarkThemeColors.onSecondary,
    secondaryContainer = DarkThemeColors.secondaryContainer,
    onSecondaryContainer = DarkThemeColors.onSecondaryContainer,
    tertiary = DarkThemeColors.tertiary,
    onTertiary = DarkThemeColors.onTertiary,
    tertiaryContainer = DarkThemeColors.tertiaryContainer,
    onTertiaryContainer = DarkThemeColors.onTertiaryContainer,
    background = DarkThemeColors.background,
    onBackground = DarkThemeColors.onBackground,
    surface = DarkThemeColors.surface,
    onSurface = DarkThemeColors.onSurface,
    surfaceVariant = DarkThemeColors.surfaceVariant,
    onSurfaceVariant = DarkThemeColors.onSurfaceVariant,
    error = DarkThemeColors.error,
    onError = DarkThemeColors.onError,
    errorContainer = DarkThemeColors.errorContainer,
    onErrorContainer = DarkThemeColors.onErrorContainer,
    outline = DarkThemeColors.outline,
    outlineVariant = DarkThemeColors.outlineVariant,
)

// Glass Glow Theme Structure with reusable shapes
object GlassGlowTheme {
    // Predefined shapes for glass glow components
    @Suppress("unused")
    val shapes = GlassGlowShapes()
}

data class GlassGlowShapes(
    val small: RoundedCornerShape = RoundedCornerShape(8.dp),
    val medium: RoundedCornerShape = RoundedCornerShape(12.dp),
    val large: RoundedCornerShape = RoundedCornerShape(16.dp),
    val extraLarge: RoundedCornerShape = RoundedCornerShape(24.dp),
    val full: RoundedCornerShape = RoundedCornerShape(50),
)

// Composable Theme Provider
@Composable
fun AuriApplicationTheme(
    isDarkTheme: Boolean = LocalThemeIsDark.current.value,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) createDarkColorScheme() else createLightColorScheme()

    MaterialTheme(
        colorScheme = colorScheme,
    ) {
        CompositionLocalProvider(
            LocalGlassGlowTheme provides GlassGlowTheme,
            content = content
        )
    }
}

// Composition Local for Theme State
val LocalThemeIsDark = compositionLocalOf<MutableState<Boolean>> {
    error("No ThemeIsDark provided")
}

// Composition Local for Glass Glow Theme
val LocalGlassGlowTheme = compositionLocalOf { GlassGlowTheme }

// Extension function for easy glass surface modifier
fun Modifier.glassGlow(
    color: Color = Color.White.copy(alpha = 0.1f),
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
): Modifier = this
    .background(
        color = color,
        shape = shape
    )





