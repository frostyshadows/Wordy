package com.sherryyuan.wordy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = RibbonRed,
    secondary = Mocha,
    tertiary = LightGray,
    background = PaperWhite,
    surface = CreamyWhite,
    primaryContainer = CreamyWhite,
    secondaryContainer = Gold,
    surfaceContainer = CreamyWhite,
    surfaceContainerHighest = CreamyWhite,
)

private val DarkColorScheme = darkColorScheme(
    primary = RibbonPink,
    secondary = PaperWhite,
    tertiary = LightGray,
    background = DarkGray,
    surface = DarkerGray,
    primaryContainer = DarkerGray,
    secondaryContainer = Latte,
    surfaceContainer = DarkerGray,
    surfaceContainerHighest = DarkerGray,
)

@Composable
fun WordyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
