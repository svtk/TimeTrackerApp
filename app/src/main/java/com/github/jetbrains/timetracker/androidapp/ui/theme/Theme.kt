package com.github.jetbrains.timetracker.androidapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = indigoLight,
    primaryVariant = indigoMain,
    secondary = orangeMain,
)

private val LightColorPalette = lightColors(
    primary = indigoMain,
    primaryVariant = indigoLight,
    secondary = orangeDark,
    secondaryVariant = orangeDark,
    background = lightGrey,
    onSecondary = Color.White,
    /* Other default colors to override
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun TimeTrackerAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}