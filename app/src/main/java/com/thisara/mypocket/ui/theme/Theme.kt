package com.thisara.mypocket.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.thisara.mypocket.data.ThemeMode

val PocketElectric = Color(0xFF0A84FF)
val PocketRoyal = Color(0xFF3056F6)
val PocketMidnight = Color(0xFF141416)
val PocketVoid = Color(0xFF050506)

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
        Color(0xFF050506),
        Color(0xFF0B0B0D),
        Color(0xFF141416),
        Color(0xFF070708),
    ),
    appIconGradient = listOf(
        Color(0xFF2EA8FF),
        Color(0xFF3056F6),
        Color(0xFF8F2FD9),
    ),
    glass = Color.White.copy(alpha = 0.08f),
    glassStrong = Color.White.copy(alpha = 0.14f),
    glassSelected = Color.White.copy(alpha = 0.20f),
    glassStroke = Color.White.copy(alpha = 0.18f),
    glassStrokeStrong = Color.White.copy(alpha = 0.36f),
    navigationGlass = Color(0xE6080809),
    progressTrack = Color.White.copy(alpha = 0.12f),
    textMuted = Color(0xFFC8C8CF),
    openCell = Color.White.copy(alpha = 0.12f),
    savedCell = Color(0xFF0A84FF),
    lockedCell = Color.White.copy(alpha = 0.06f),
    openCellContent = Color(0xFFF7F7F9),
    savedCellContent = Color.White,
    lockedCellContent = Color(0x99E6E6EA),
    loadingOverlay = Color.Black.copy(alpha = 0.70f),
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
    primary = Color(0xFF0A84FF),
    onPrimary = Color.White,
    secondary = Color(0xFFFF5B8C),
    onSecondary = Color.White,
    tertiary = Color(0xFFFFD948),
    onTertiary = Color(0xFF141416),
    background = PocketVoid,
    onBackground = Color(0xFFF7F7F9),
    surface = Color(0xFF111114),
    onSurface = Color(0xFFF7F7F9),
    surfaceVariant = Color(0xFF1C1C20),
    onSurfaceVariant = Color(0xFFC8C8CF),
    outline = Color.White.copy(alpha = 0.24f),
    error = Color(0xFFFF453A),
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
fun MyPocketTheme(themeMode: ThemeMode = ThemeMode.SYSTEM, content: @Composable () -> Unit) {
    val darkTheme = resolveDarkTheme(themeMode = themeMode)
    val colorScheme = if (darkTheme) DarkGlassColors else LightGlassColors
    val styleColors = if (darkTheme) DarkPocketStyle else LightPocketStyle

    CompositionLocalProvider(LocalPocketStyle provides styleColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = PocketShapes,
            typography = MaterialTheme.typography,
            content = content,
        )
    }
}

@Composable
fun resolveDarkTheme(themeMode: ThemeMode): Boolean {
    return when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
}
