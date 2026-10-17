package com.namabiz.agrim.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val WarmBackground = Color(0xFFF9F6F0)
val SurfaceCard = Color(0xFFFFFFFF)
val PrimaryDark = Color(0xFF1E232A)
val BrandGreen = Color(0xFF164E43)
val BrandGreenLight = Color(0xFFE6F4EA)
val AccentGreen = Color(0xFF10B981)
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B7280)
val TextMuted = Color(0xFF9CA3AF)
val BorderLight = Color(0xFFE5E7EB)
val AmberBadgeBg = Color(0xFFFEF3C7)
val AmberBadgeText = Color(0xFF92400E)
val LightGrayChip = Color(0xFFF3F4F6)
val BackgroundLight = WarmBackground
val TextDark = TextPrimary

private val AgrimColorScheme = lightColorScheme(
    primary = PrimaryDark,
    onPrimary = Color.White,
    primaryContainer = BrandGreen,
    onPrimaryContainer = Color.White,
    secondary = BrandGreen,
    onSecondary = Color.White,
    background = WarmBackground,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    outline = BorderLight
)

val AgrimTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp
    )
)

@Composable
fun AgrimTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AgrimColorScheme,
        typography = AgrimTypography,
        content = content
    )
}
