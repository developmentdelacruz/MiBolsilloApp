package com.delacruz.mibolsilloapp.core.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta de marca — verde azulado ("fintech"), fija (no dynamic color) para mantener
// identidad consistente entre dispositivos, como YNAB/Mint.
val Teal10 = Color(0xFF00201C)
val Teal30 = Color(0xFF005048)
val Teal40 = Color(0xFF006A5F)
val Teal80 = Color(0xFF4FDBC4)
val Teal90 = Color(0xFF7BF8E0)

val TealGrey10 = Color(0xFF191D1C)
val TealGrey30 = Color(0xFF334B47)
val TealGrey40 = Color(0xFF4A6360)
val TealGrey80 = Color(0xFFB1CCC7)
val TealGrey90 = Color(0xFFCDE8E3)

val Amber40 = Color(0xFF7A5900)
val Amber80 = Color(0xFFF6BD3E)
val Amber90 = Color(0xFFFFDEA1)
val Amber10 = Color(0xFF261A00)

val Error40 = Color(0xFFB3261E)
val Error80 = Color(0xFFF2B8B5)
val Error10 = Color(0xFF410E0B)
val Error90 = Color(0xFFF9DEDC)

val Neutral10 = Color(0xFF191C1B)
val Neutral20 = Color(0xFF2E312F)
val Neutral90 = Color(0xFFE1E3E1)
val Neutral95 = Color(0xFFEFF1EF)
val Neutral99 = Color(0xFFFBFDFB)
val NeutralVariant30 = Color(0xFF3F4947)
val NeutralVariant50 = Color(0xFF6F7977)
val NeutralVariant80 = Color(0xFFBEC9C6)
val NeutralVariant90 = Color(0xFFDCE5E2)

val Amber30 = Color(0xFF5C4200)
val Error30 = Color(0xFF8C1D18)

// Roles de superficie (M3): escala completa para que ningún Card/Chip/indicador
// caiga de nuevo en el morado por defecto de lightColorScheme()/darkColorScheme().
val SurfaceDimLight = Color(0xFFD9DBD9)
val SurfaceBrightLight = Color(0xFFF9FBF9)
val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF3F5F3)
val SurfaceContainerLight = Color(0xFFEDF0EE)
val SurfaceContainerHighLight = Color(0xFFE7EAE8)
val SurfaceContainerHighestLight = Color(0xFFE1E4E2)

val SurfaceDimDark = Color(0xFF0F1312)
val SurfaceBrightDark = Color(0xFF353A38)
val SurfaceContainerLowestDark = Color(0xFF0B0F0E)
val SurfaceContainerLowDark = Color(0xFF191C1B)
val SurfaceContainerDark = Color(0xFF1D2120)
val SurfaceContainerHighDark = Color(0xFF272B2A)
val SurfaceContainerHighestDark = Color(0xFF323735)

// Colores semánticos ingreso/gasto — independientes del ColorScheme de Material,
// se exponen vía ExtendedColors para no forzarlos dentro de los roles primary/error.
val Green40 = Color(0xFF2E7D32)
val Green80 = Color(0xFF9CD67D)
val Red40 = Error40
val Red80 = Error80
