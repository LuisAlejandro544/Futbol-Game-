package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.Player
import com.example.model.Position
import kotlin.math.abs

@Composable
fun ProceduralPlayerFace(
    player: Player,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp
) {
    ProceduralPlayerFace(
        seedString = "${player.id}_${player.fullName}",
        position = player.position,
        modifier = modifier,
        size = size
    )
}

@Composable
fun ProceduralPlayerFace(
    seedString: String,
    position: Position = Position.MID,
    modifier: Modifier = Modifier,
    size: Dp = 52.dp
) {
    val seed = remember(seedString) { abs(seedString.hashCode()) }

    val skinTones = listOf(
        Color(0xFFF5D0C5), // Light
        Color(0xFFE0AC69), // Tan
        Color(0xFFC68642), // Warm Bronze
        Color(0xFF8D5524), // Dark Bronze
        Color(0xFF523315)  // Deep Brown
    )
    val skinColor = skinTones[seed % skinTones.size]

    val hairColors = listOf(
        Color(0xFF121212), // Black
        Color(0xFF382319), // Dark Brown
        Color(0xFF6E432B), // Medium Brown
        Color(0xFFD2A153), // Blonde
        Color(0xFF8C3426), // Reddish
        Color(0xFF9E9E9E)  // Gray / Platinum
    )
    val hairColor = hairColors[(seed / 5) % hairColors.size]

    val hairStyle = (seed / 11) % 5 // 0: Short, 1: Spiky, 2: Curly Top, 3: Slick, 4: Headband

    val shirtColor = when (position) {
        Position.GK -> Color(0xFFFFB300)  // Gold/Amber
        Position.DEF -> Color(0xFF1E88E5) // Blue
        Position.MID -> Color(0xFF43A047) // Emerald
        Position.ATT -> Color(0xFFE53935) // Crimson Red
    }

    val eyeColor = Color(0xFF111111)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF151D2A))
            .border(1.5.dp, shirtColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = size.toPx()
            val h = size.toPx()
            val centerX = w / 2f
            val centerY = h / 2f

            // 1. Shirt / Collar at bottom
            drawArc(
                color = shirtColor,
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(centerX - w * 0.42f, h * 0.62f),
                size = Size(w * 0.84f, h * 0.5f)
            )

            // Inner V-neck
            val neckPath = Path().apply {
                moveTo(centerX - w * 0.15f, h * 0.62f)
                lineTo(centerX, h * 0.78f)
                lineTo(centerX + w * 0.15f, h * 0.62f)
                close()
            }
            drawPath(neckPath, skinColor)

            // 2. Neck
            drawRect(
                color = skinColor,
                topLeft = Offset(centerX - w * 0.12f, h * 0.52f),
                size = Size(w * 0.24f, h * 0.16f)
            )

            // 3. Head / Face Oval
            val headWidth = w * 0.52f
            val headHeight = h * 0.58f
            val headTopLeft = Offset(centerX - headWidth / 2f, h * 0.18f)
            drawOval(
                color = skinColor,
                topLeft = headTopLeft,
                size = Size(headWidth, headHeight)
            )

            // Ears
            drawCircle(
                color = skinColor,
                radius = w * 0.06f,
                center = Offset(centerX - headWidth / 2f - 1f, h * 0.44f)
            )
            drawCircle(
                color = skinColor,
                radius = w * 0.06f,
                center = Offset(centerX + headWidth / 2f + 1f, h * 0.44f)
            )

            // 4. Eyes
            val eyeOffsetY = h * 0.42f
            val eyeSpacing = w * 0.12f
            drawCircle(color = eyeColor, radius = w * 0.032f, center = Offset(centerX - eyeSpacing, eyeOffsetY))
            drawCircle(color = eyeColor, radius = w * 0.032f, center = Offset(centerX + eyeSpacing, eyeOffsetY))
            // Eye shine
            drawCircle(color = Color.White, radius = w * 0.012f, center = Offset(centerX - eyeSpacing - 1f, eyeOffsetY - 1f))
            drawCircle(color = Color.White, radius = w * 0.012f, center = Offset(centerX + eyeSpacing - 1f, eyeOffsetY - 1f))

            // Eyebrows
            val browY = h * 0.37f
            val browPathLeft = Path().apply {
                moveTo(centerX - eyeSpacing - w * 0.05f, browY + 1f)
                lineTo(centerX - eyeSpacing + w * 0.05f, browY)
            }
            val browPathRight = Path().apply {
                moveTo(centerX + eyeSpacing - w * 0.05f, browY)
                lineTo(centerX + eyeSpacing + w * 0.05f, browY + 1f)
            }
            drawPath(browPathLeft, hairColor, style = Stroke(width = w * 0.03f))
            drawPath(browPathRight, hairColor, style = Stroke(width = w * 0.03f))

            // 5. Nose
            val nosePath = Path().apply {
                moveTo(centerX, h * 0.46f)
                lineTo(centerX - w * 0.02f, h * 0.52f)
                lineTo(centerX + w * 0.03f, h * 0.52f)
            }
            drawPath(nosePath, skinColor.copy(alpha = 0.8f), style = Stroke(width = w * 0.02f))

            // 6. Mouth / Smile
            val mouthPath = Path().apply {
                moveTo(centerX - w * 0.08f, h * 0.60f)
                quadraticTo(centerX, h * 0.65f, centerX + w * 0.08f, h * 0.60f)
            }
            drawPath(mouthPath, Color(0xFF8A3B3B), style = Stroke(width = w * 0.028f))

            // 7. Hair Styles
            when (hairStyle) {
                0 -> { // Short Crew Cut
                    drawArc(
                        color = hairColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(centerX - headWidth / 2f - 2f, h * 0.14f),
                        size = Size(headWidth + 4f, headHeight * 0.5f)
                    )
                }
                1 -> { // Spiky Hair
                    val spikyPath = Path().apply {
                        moveTo(centerX - headWidth / 2f, h * 0.32f)
                        lineTo(centerX - headWidth * 0.4f, h * 0.12f)
                        lineTo(centerX - headWidth * 0.2f, h * 0.22f)
                        lineTo(centerX, h * 0.08f)
                        lineTo(centerX + headWidth * 0.2f, h * 0.22f)
                        lineTo(centerX + headWidth * 0.4f, h * 0.12f)
                        lineTo(centerX + headWidth / 2f, h * 0.32f)
                        close()
                    }
                    drawPath(spikyPath, hairColor)
                }
                2 -> { // Curly / Afro top
                    drawCircle(color = hairColor, radius = w * 0.18f, center = Offset(centerX - w * 0.12f, h * 0.22f))
                    drawCircle(color = hairColor, radius = w * 0.18f, center = Offset(centerX + w * 0.12f, h * 0.22f))
                    drawCircle(color = hairColor, radius = w * 0.20f, center = Offset(centerX, h * 0.18f))
                }
                3 -> { // Slicked Back
                    val slickPath = Path().apply {
                        moveTo(centerX - headWidth / 2f - 2f, h * 0.35f)
                        quadraticTo(centerX - headWidth / 2f, h * 0.12f, centerX, h * 0.12f)
                        quadraticTo(centerX + headWidth / 2f, h * 0.12f, centerX + headWidth / 2f + 2f, h * 0.35f)
                        lineTo(centerX + headWidth / 2f, h * 0.28f)
                        quadraticTo(centerX, h * 0.20f, centerX - headWidth / 2f, h * 0.28f)
                        close()
                    }
                    drawPath(slickPath, hairColor)
                }
                else -> { // Headband / Buzz
                    drawArc(
                        color = hairColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(centerX - headWidth / 2f, h * 0.16f),
                        size = Size(headWidth, headHeight * 0.4f)
                    )
                    // Headband ribbon
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(centerX - headWidth / 2f - 1f, h * 0.30f),
                        size = Size(headWidth + 2f, h * 0.05f)
                    )
                }
            }

            // Optional Facial Hair (for ~30% of players)
            if ((seed / 13) % 3 == 0) {
                val beardPath = Path().apply {
                    moveTo(centerX - w * 0.12f, h * 0.58f)
                    quadraticTo(centerX, h * 0.72f, centerX + w * 0.12f, h * 0.58f)
                    quadraticTo(centerX, h * 0.68f, centerX - w * 0.12f, h * 0.58f)
                }
                drawPath(beardPath, hairColor.copy(alpha = 0.85f))
            }
        }
    }
}
