package com.thisara.mypocket.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val PocketElectric = Color(0xFF2F2FE4)
val PocketRoyal = Color(0xFF162E93)
val PocketMidnight = Color(0xFF1A1953)
val PocketVoid = Color(0xFF080616)

val PocketBlue = PocketElectric
val PocketYellow = Color(0xFFFFD928)
val PocketOrange = Color(0xFFFF6A2A)
val PocketRose = Color(0xFFD91B59)
val PocketInk = Color(0xFF0C1229)
val PocketPaper = Color(0xFFF4F5FB)
val PocketMist = Color(0xFFEAF0FF)
val PocketGlass = Color(0xEFFFFFFF)
val PocketGlassStrong = Color(0xF7FFFFFF)
val PocketGlassStroke = Color(0xFFDCE2F0)
val PocketTextMuted = Color(0xFF5D6675)

@Immutable
data class PocketStyleColors(
    val isDark: Boolean,
    val backgroundGradient: List<Color>,
    val appIconGradient: List<Color>,
    val glass: Color,
    val glassStrong: Color,
    val glassSelected: Color,
    val glassStroke: Color,
    val glassStrokeStrong: Color,
    val navigationGlass: Color,
    val progressTrack: Color,
    val textMuted: Color,
    val openCell: Color,
    val savedCell: Color,
    val lockedCell: Color,
    val openCellContent: Color,
    val savedCellContent: Color,
    val lockedCellContent: Color,
    val loadingOverlay: Color,
)

private val LightPocketStyle = PocketStyleColors(
    isDark = false,
    backgroundGradient = listOf(
        Color(0xFFF8F8FA),
        Color(0xFFECEFF7),
        Color(0xFFFFFFFF),
    ),
    appIconGradient = listOf(
        Color(0xFF06B6D4),
        PocketElectric,
        Color(0xFFEF2F8B),
    ),
    glass = Color.White.copy(alpha = 0.82f),
    glassStrong = Color.White.copy(alpha = 0.96f),
    glassSelected = Color(0xFFE8F5FF),
    glassStroke = Color(0xFFD7DDEB),
    glassStrokeStrong = Color(0xFF8FA2FF),
    navigationGlass = Color.White.copy(alpha = 0.96f),
    progressTrack = Color(0xFFE6EBF6),
    textMuted = Color(0xFF5D6675),
    openCell = Color.White.copy(alpha = 0.94f),
    savedCell = PocketElectric,
    lockedCell = Color(0xFFE6E9F2),
    openCellContent = PocketInk,
    savedCellContent = Color.White,
    lockedCellContent = Color(0xFF858B99),
    loadingOverlay = Color.White.copy(alpha = 0.72f),
)

private val DarkPocketStyle = PocketStyleColors(
    isDark = true,
    backgroundGradient = listOf(
        PocketVoid,
        Color(0xFF0C1229),
        PocketMidnight,
        PocketVoid,
    ),
    appIconGradient = listOf(
        PocketElectric,
        Color(0xFF33D6FF),
        Color(0xFFFF4D9A),
    ),
    glass = Color(0x66303A67),
    glassStrong = Color(0xCC202744),
    glassSelected = Color(0xCC162E93),
    glassStroke = Color(0x668FA2FF),
    glassStrokeStrong = Color(0xCC8FA2FF),
    navigationGlass = Color(0xE60C1229),
    progressTrack = Color(0xFF252C4D),
    textMuted = Color(0xFFC8D0EE),
    openCell = Color(0xCC202744),
    savedCell = Color(0xFF2F65FF),
    lockedCell = Color(0x99080616),
    openCellContent = Color(0xFFF6F8FF),
    savedCellContent = Color.White,
    lockedCellContent = Color(0x99C8D0EE),
    loadingOverlay = PocketVoid.copy(alpha = 0.74f),
)

private val LocalPocketStyle = staticCompositionLocalOf { LightPocketStyle }

object PocketTheme {
    val colors: PocketStyleColors
        @Composable
        @ReadOnlyComposable
        get() = LocalPocketStyle.current
}

private val LightGlassColors: ColorScheme = lightColorScheme(
    primary = PocketElectric,
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
    onSurfaceVariant = PocketTextMuted,
    outline = PocketGlassStroke,
    error = PocketRose,
    onError = Color.White,
)

private val DarkGlassColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF5F78FF),
    onPrimary = Color.White,
    secondary = Color(0xFFFF4D9A),
    onSecondary = Color.White,
    tertiary = Color(0xFF33D6FF),
    onTertiary = Color.White,
    background = PocketVoid,
    onBackground = Color(0xFFF6F8FF),
    surface = Color(0xFF0C1229),
    onSurface = Color(0xFFF6F8FF),
    surfaceVariant = Color(0xFF202744),
    onSurfaceVariant = Color(0xFFC8D0EE),
    outline = Color(0x668FA2FF),
    error = Color(0xFFFF4D7D),
    onError = Color.White,
)

private val PocketShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp),
    small = RoundedCornerShape(16.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(34.dp),
)

@Composable
fun MyPocketTheme(darkModeEnabled: Boolean = false, content: @Composable () -> Unit) {
    val colorScheme = if (darkModeEnabled) DarkGlassColors else LightGlassColors
    val styleColors = if (darkModeEnabled) DarkPocketStyle else LightPocketStyle

    CompositionLocalProvider(LocalPocketStyle provides styleColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = PocketShapes,
            typography = MaterialTheme.typography,
            content = content,
        )
    }
}
