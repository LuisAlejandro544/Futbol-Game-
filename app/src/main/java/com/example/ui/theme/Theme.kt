package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
      primary = GrassEmerald,
      secondary = NeonAmber,
      tertiary = CardGold,
      background = PitchDarkBg,
      surface = SurfaceCarbon,
      onPrimary = PitchDarkBg,
      onSecondary = PitchDarkBg,
      onBackground = TextPrimary,
      onSurface = TextPrimary
  )

private val LightColorScheme =
  darkColorScheme( // We default to dark theme to maintain the premium simulation terminal aesthetics!
      primary = GrassEmerald,
      secondary = NeonAmber,
      tertiary = CardGold,
      background = PitchDarkBg,
      surface = SurfaceCarbon,
      onPrimary = PitchDarkBg,
      onSecondary = PitchDarkBg,
      onBackground = TextPrimary,
      onSurface = TextPrimary
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Set to false to always preserve the beautiful Midnight terminal branding
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme // Always use our custom premium sports theme


  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
