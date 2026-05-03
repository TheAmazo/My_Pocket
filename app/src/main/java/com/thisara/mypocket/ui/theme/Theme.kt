package com.thisara.mypocket.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PocketBlue = Color(0xFF03AED2)
val PocketYellow = Color(0xFFF8DE22)
val PocketOrange = Color(0xFFF45B26)
val PocketRose = Color(0xFFD12052)
val PocketInk = Color(0xFF172026)
val PocketPaper = Color(0xFFFFFBF2)
val PocketMist = Color(0xFFEAF8FB)

private val LightColors: ColorScheme = lightColorScheme(
    primary = PocketBlue,
    onPrimary = Color.White,
    secondary = PocketRose,
    onSecondary = Color.White,
    tertiary = PocketOrange,
    onTertiary = Color.White,
    background = PocketPaper,
    onBackground = PocketInk,
    surface = Color.White,
    onSurface = PocketInk,
    surfaceVariant = PocketMist,
    onSurfaceVariant = Color(0xFF41515A),
    outline = Color(0xFFB9C7CE),
)

@Composable
fun MyPocketTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}
