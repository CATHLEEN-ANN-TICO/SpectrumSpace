package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SageColorScheme = lightColorScheme(
    primary = SagePrimary,
    onPrimary = SageOnPrimary,
    primaryContainer = SageContainer,
    onPrimaryContainer = SageOnContainer,
    secondary = SageSecondary,
    background = SageBackground,
    surface = SageSurface,
    onBackground = Color(0xFF1B241E),
    onSurface = Color(0xFF1B241E),
    outline = SageCardOutline
)

private val SandColorScheme = lightColorScheme(
    primary = SandPrimary,
    onPrimary = SandOnPrimary,
    primaryContainer = SandContainer,
    onPrimaryContainer = SandOnContainer,
    secondary = SandSecondary,
    background = SandBackground,
    surface = SandSurface,
    onBackground = Color(0xFF261D14),
    onSurface = Color(0xFF261D14),
    outline = SandCardOutline
)

private val LavenderColorScheme = lightColorScheme(
    primary = LavenderPrimary,
    onPrimary = LavenderOnPrimary,
    primaryContainer = LavenderContainer,
    onPrimaryContainer = LavenderOnContainer,
    secondary = LavenderSecondary,
    background = LavenderBackground,
    surface = LavenderSurface,
    onBackground = Color(0xFF1B1B29),
    onSurface = Color(0xFF1B1B29),
    outline = LavenderCardOutline
)

private val DuskColorScheme = darkColorScheme(
    primary = DuskPrimary,
    onPrimary = DuskOnPrimary,
    primaryContainer = DuskContainer,
    onPrimaryContainer = DuskOnContainer,
    secondary = DuskSecondary,
    background = DuskBackground,
    surface = DuskSurface,
    onBackground = Color(0xFFECEFEA),
    onSurface = Color(0xFFECEFEA),
    outline = DuskCardOutline
)

@Composable
fun CalmSpaceTheme(
    paletteKey: String = "SAGE",
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseScheme = when (paletteKey.uppercase()) {
        "SAND" -> SandColorScheme
        "LAVENDER" -> LavenderColorScheme
        "DUSK" -> DuskColorScheme
        else -> SageColorScheme
    }

    val finalScheme = if (highContrast) {
        baseScheme.copy(
            outline = Color(0xFF111111),
            onBackground = Color(0xFF000000),
            onSurface = Color(0xFF000000)
        )
    } else {
        baseScheme
    }

    MaterialTheme(
        colorScheme = finalScheme,
        typography = Typography,
        content = content
    )
}
