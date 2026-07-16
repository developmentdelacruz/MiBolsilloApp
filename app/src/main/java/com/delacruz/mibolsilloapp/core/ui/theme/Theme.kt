package com.delacruz.mibolsilloapp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Colores semánticos ingreso/gasto, fuera de los roles primary/error de Material. */
data class ExtendedColors(
    val positive: Color,
    val onPositive: Color,
    val negative: Color,
    val onNegative: Color,
)

private val LightExtendedColors = ExtendedColors(
    positive = Green40,
    onPositive = Color.White,
    negative = Red40,
    onNegative = Color.White,
)

private val DarkExtendedColors = ExtendedColors(
    positive = Green80,
    onPositive = Color.Black,
    negative = Red80,
    onNegative = Color.Black,
)

private val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

// ColorScheme completo a propósito: lightColorScheme()/darkColorScheme() rellenan con la
// paleta morada por defecto de Material cualquier rol que no se especifique acá (Container,
// outline, surfaceContainer*, etc.) — dejar roles sin definir es lo que causaba que el
// indicador del NavigationBar y varias Cards se vieran moradas pese a la paleta teal.
private val DarkColorScheme = darkColorScheme(
    primary = Teal80,
    onPrimary = Teal10,
    primaryContainer = Teal30,
    onPrimaryContainer = Teal90,
    secondary = TealGrey80,
    onSecondary = TealGrey10,
    secondaryContainer = TealGrey30,
    onSecondaryContainer = TealGrey90,
    tertiary = Amber80,
    onTertiary = Amber10,
    tertiaryContainer = Amber30,
    onTertiaryContainer = Amber90,
    error = Error80,
    onError = Error10,
    errorContainer = Error30,
    onErrorContainer = Error90,
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant30,
    scrim = Color.Black,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,
    inversePrimary = Teal40,
    surfaceTint = Teal80,
    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark,
)

private val LightColorScheme = lightColorScheme(
    primary = Teal40,
    onPrimary = Color.White,
    primaryContainer = Teal90,
    onPrimaryContainer = Teal10,
    secondary = TealGrey40,
    onSecondary = Color.White,
    secondaryContainer = TealGrey90,
    onSecondaryContainer = TealGrey10,
    tertiary = Amber40,
    onTertiary = Color.White,
    tertiaryContainer = Amber90,
    onTertiaryContainer = Amber10,
    error = Error40,
    onError = Color.White,
    errorContainer = Error90,
    onErrorContainer = Error10,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
    scrim = Color.Black,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    inversePrimary = Teal80,
    surfaceTint = Teal40,
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
)

@Composable
fun MiBolsilloAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}

/** Acceso a colores fuera del ColorScheme estándar, al estilo de MaterialTheme.colorScheme. */
object MiBolsilloTheme {
    val extendedColors: ExtendedColors
        @Composable get() = LocalExtendedColors.current
}
