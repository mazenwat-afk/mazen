package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.AppThemeStyle

private fun getThemeColorScheme(style: AppThemeStyle, darkTheme: Boolean): ColorScheme {
    return when (style) {
        AppThemeStyle.CLASSIC -> if (darkTheme) {
            darkColorScheme(
                primary = ClassicPrimaryDark,
                onPrimary = ClassicOnPrimaryDark,
                primaryContainer = ClassicPrimaryContainerDark,
                onPrimaryContainer = Color(0xFFE2E8F0),
                secondary = ClassicSecondaryDark,
                onSecondary = Color(0xFF0F172A),
                tertiary = ClassicTertiaryDark,
                background = ClassicBackgroundDark,
                onBackground = Color(0xFFF1F5F9),
                surface = ClassicSurfaceDark,
                onSurface = Color(0xFFF8FAFC),
                surfaceVariant = ClassicSurfaceVariantDark,
                onSurfaceVariant = Color(0xFFCBD5E1)
            )
        } else {
            lightColorScheme(
                primary = ClassicPrimaryLight,
                onPrimary = ClassicOnPrimaryLight,
                primaryContainer = ClassicPrimaryContainerLight,
                onPrimaryContainer = ClassicPrimaryLight,
                secondary = ClassicSecondaryLight,
                onSecondary = Color.White,
                tertiary = ClassicTertiaryLight,
                background = ClassicBackgroundLight,
                onBackground = Color(0xFF0F172A),
                surface = ClassicSurfaceLight,
                onSurface = Color(0xFF0F172A),
                surfaceVariant = ClassicSurfaceVariantLight,
                onSurfaceVariant = Color(0xFF475569)
            )
        }

        AppThemeStyle.OCEAN -> if (darkTheme) {
            darkColorScheme(
                primary = OceanPrimaryDark,
                onPrimary = OceanOnPrimaryDark,
                primaryContainer = OceanPrimaryContainerDark,
                onPrimaryContainer = Color(0xFFE0F2FE),
                secondary = OceanSecondaryDark,
                onSecondary = Color(0xFF082F49),
                tertiary = OceanTertiaryDark,
                background = OceanBackgroundDark,
                onBackground = Color(0xFFE0F2FE),
                surface = OceanSurfaceDark,
                onSurface = Color(0xFFF0FDF4),
                surfaceVariant = OceanSurfaceVariantDark,
                onSurfaceVariant = Color(0xFFBAE6FD)
            )
        } else {
            lightColorScheme(
                primary = OceanPrimaryLight,
                onPrimary = OceanOnPrimaryLight,
                primaryContainer = OceanPrimaryContainerLight,
                onPrimaryContainer = OceanPrimaryLight,
                secondary = OceanSecondaryLight,
                onSecondary = Color.White,
                tertiary = OceanTertiaryLight,
                background = OceanBackgroundLight,
                onBackground = Color(0xFF082F49),
                surface = OceanSurfaceLight,
                onSurface = Color(0xFF082F49),
                surfaceVariant = OceanSurfaceVariantLight,
                onSurfaceVariant = Color(0xFF155E75)
            )
        }

        AppThemeStyle.EMERALD -> if (darkTheme) {
            darkColorScheme(
                primary = EmeraldPrimaryDark,
                onPrimary = EmeraldOnPrimaryDark,
                primaryContainer = EmeraldPrimaryContainerDark,
                onPrimaryContainer = Color(0xFFD1FAE5),
                secondary = EmeraldSecondaryDark,
                onSecondary = Color(0xFF022C22),
                tertiary = EmeraldTertiaryDark,
                background = EmeraldBackgroundDark,
                onBackground = Color(0xFFECFDF5),
                surface = EmeraldSurfaceDark,
                onSurface = Color(0xFFF0FDF4),
                surfaceVariant = EmeraldSurfaceVariantDark,
                onSurfaceVariant = Color(0xFFA7F3D0)
            )
        } else {
            lightColorScheme(
                primary = EmeraldPrimaryLight,
                onPrimary = EmeraldOnPrimaryLight,
                primaryContainer = EmeraldPrimaryContainerLight,
                onPrimaryContainer = EmeraldPrimaryLight,
                secondary = EmeraldSecondaryLight,
                onSecondary = Color.White,
                tertiary = EmeraldTertiaryLight,
                background = EmeraldBackgroundLight,
                onBackground = Color(0xFF022C22),
                surface = EmeraldSurfaceLight,
                onSurface = Color(0xFF022C22),
                surfaceVariant = EmeraldSurfaceVariantLight,
                onSurfaceVariant = Color(0xFF065F46)
            )
        }

        AppThemeStyle.PURPLE -> if (darkTheme) {
            darkColorScheme(
                primary = PurplePrimaryDark,
                onPrimary = PurpleOnPrimaryDark,
                primaryContainer = PurplePrimaryContainerDark,
                onPrimaryContainer = Color(0xFFEDE9FE),
                secondary = PurpleSecondaryDark,
                onSecondary = Color(0xFF2E1065),
                tertiary = PurpleTertiaryDark,
                background = PurpleBackgroundDark,
                onBackground = Color(0xFFF5F3FF),
                surface = PurpleSurfaceDark,
                onSurface = Color(0xFFFAF5FF),
                surfaceVariant = PurpleSurfaceVariantDark,
                onSurfaceVariant = Color(0xFFDDD6FE)
            )
        } else {
            lightColorScheme(
                primary = PurplePrimaryLight,
                onPrimary = PurpleOnPrimaryLight,
                primaryContainer = PurplePrimaryContainerLight,
                onPrimaryContainer = PurplePrimaryLight,
                secondary = PurpleSecondaryLight,
                onSecondary = Color.White,
                tertiary = PurpleTertiaryLight,
                background = PurpleBackgroundLight,
                onBackground = Color(0xFF1E112A),
                surface = PurpleSurfaceLight,
                onSurface = Color(0xFF1E112A),
                surfaceVariant = PurpleSurfaceVariantLight,
                onSurfaceVariant = Color(0xFF5B21B6)
            )
        }

        AppThemeStyle.SUNSET -> if (darkTheme) {
            darkColorScheme(
                primary = SunsetPrimaryDark,
                onPrimary = SunsetOnPrimaryDark,
                primaryContainer = SunsetPrimaryContainerDark,
                onPrimaryContainer = Color(0xFFFEF3C7),
                secondary = SunsetSecondaryDark,
                onSecondary = Color(0xFF451A03),
                tertiary = SunsetTertiaryDark,
                background = SunsetBackgroundDark,
                onBackground = Color(0xFFFFFBEB),
                surface = SunsetSurfaceDark,
                onSurface = Color(0xFFFFFBEB),
                surfaceVariant = SunsetSurfaceVariantDark,
                onSurfaceVariant = Color(0xFFFDE68A)
            )
        } else {
            lightColorScheme(
                primary = SunsetPrimaryLight,
                onPrimary = SunsetOnPrimaryLight,
                primaryContainer = SunsetPrimaryContainerLight,
                onPrimaryContainer = SunsetPrimaryLight,
                secondary = SunsetSecondaryLight,
                onSecondary = Color.White,
                tertiary = SunsetTertiaryLight,
                background = SunsetBackgroundLight,
                onBackground = Color(0xFF451A03),
                surface = SunsetSurfaceLight,
                onSurface = Color(0xFF451A03),
                surfaceVariant = SunsetSurfaceVariantLight,
                onSurfaceVariant = Color(0xFF92400E)
            )
        }

        AppThemeStyle.RUBY -> if (darkTheme) {
            darkColorScheme(
                primary = RubyPrimaryDark,
                onPrimary = RubyOnPrimaryDark,
                primaryContainer = RubyPrimaryContainerDark,
                onPrimaryContainer = Color(0xFFFFE4E6),
                secondary = RubySecondaryDark,
                onSecondary = Color(0xFF4C0519),
                tertiary = RubyTertiaryDark,
                background = RubyBackgroundDark,
                onBackground = Color(0xFFFFF1F2),
                surface = RubySurfaceDark,
                onSurface = Color(0xFFFFF1F2),
                surfaceVariant = RubySurfaceVariantDark,
                onSurfaceVariant = Color(0xFFFECDD3)
            )
        } else {
            lightColorScheme(
                primary = RubyPrimaryLight,
                onPrimary = RubyOnPrimaryLight,
                primaryContainer = RubyPrimaryContainerLight,
                onPrimaryContainer = RubyPrimaryLight,
                secondary = RubySecondaryLight,
                onSecondary = Color.White,
                tertiary = RubyTertiaryLight,
                background = RubyBackgroundLight,
                onBackground = Color(0xFF4C0519),
                surface = RubySurfaceLight,
                onSurface = Color(0xFF4C0519),
                surfaceVariant = RubySurfaceVariantLight,
                onSurfaceVariant = Color(0xFF9F1239)
            )
        }

        AppThemeStyle.MIDNIGHT_GOLD -> if (darkTheme) {
            darkColorScheme(
                primary = MidnightGoldPrimaryDark,
                onPrimary = MidnightGoldOnPrimaryDark,
                primaryContainer = MidnightGoldPrimaryContainerDark,
                onPrimaryContainer = Color(0xFFFEF08A),
                secondary = MidnightGoldSecondaryDark,
                onSecondary = Color(0xFF422006),
                tertiary = MidnightGoldTertiaryDark,
                background = MidnightGoldBackgroundDark,
                onBackground = Color(0xFFFAFAF9),
                surface = MidnightGoldSurfaceDark,
                onSurface = Color(0xFFFAFAF9),
                surfaceVariant = MidnightGoldSurfaceVariantDark,
                onSurfaceVariant = Color(0xFFE7E5E4)
            )
        } else {
            lightColorScheme(
                primary = MidnightGoldPrimaryLight,
                onPrimary = MidnightGoldOnPrimaryLight,
                primaryContainer = MidnightGoldPrimaryContainerLight,
                onPrimaryContainer = MidnightGoldPrimaryLight,
                secondary = MidnightGoldSecondaryLight,
                onSecondary = Color.White,
                tertiary = MidnightGoldTertiaryLight,
                background = MidnightGoldBackgroundLight,
                onBackground = Color(0xFF422006),
                surface = MidnightGoldSurfaceLight,
                onSurface = Color(0xFF422006),
                surfaceVariant = MidnightGoldSurfaceVariantLight,
                onSurfaceVariant = Color(0xFFA16207)
            )
        }

        AppThemeStyle.FOREST -> if (darkTheme) {
            darkColorScheme(
                primary = ForestPrimaryDark,
                onPrimary = ForestOnPrimaryDark,
                primaryContainer = ForestPrimaryContainerDark,
                onPrimaryContainer = Color(0xFFDCFCE7),
                secondary = ForestSecondaryDark,
                onSecondary = Color(0xFF14532D),
                tertiary = ForestTertiaryDark,
                background = ForestBackgroundDark,
                onBackground = Color(0xFFF7FEE7),
                surface = ForestSurfaceDark,
                onSurface = Color(0xFFF7FEE7),
                surfaceVariant = ForestSurfaceVariantDark,
                onSurfaceVariant = Color(0xFFBBF7D0)
            )
        } else {
            lightColorScheme(
                primary = ForestPrimaryLight,
                onPrimary = ForestOnPrimaryLight,
                primaryContainer = ForestPrimaryContainerLight,
                onPrimaryContainer = ForestPrimaryLight,
                secondary = ForestSecondaryLight,
                onSecondary = Color.White,
                tertiary = ForestTertiaryLight,
                background = ForestBackgroundLight,
                onBackground = Color(0xFF14532D),
                surface = ForestSurfaceLight,
                onSurface = Color(0xFF14532D),
                surfaceVariant = ForestSurfaceVariantLight,
                onSurfaceVariant = Color(0xFF3F6212)
            )
        }
    }
}

@Composable
fun MazenLedgerTheme(
    style: AppThemeStyle = AppThemeStyle.CLASSIC,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = getThemeColorScheme(style, darkTheme)
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    MazenLedgerTheme(
        style = AppThemeStyle.CLASSIC,
        darkTheme = darkTheme,
        content = content
    )
}
