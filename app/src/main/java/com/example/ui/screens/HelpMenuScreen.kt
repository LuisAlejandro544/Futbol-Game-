package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HelpMenuScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchDarkBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxHeight()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceCarbon, RoundedCornerShape(10.dp))
                        .testTag("help_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = GlacierBlue
                    )
                }

                Text(
                    text = "AYUDA Y REGLAS",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            HorizontalDivider(color = DarkSteel)

            // Content Scroll Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HelpItemCard(
                    title = "🌌 Universo Procedural",
                    content = "FEDEBOL Manager crea un mundo de fútbol completamente ficticio y dinámico desde cero. No contiene marcas, ligas ni jugadores reales, protegiendo la identidad del juego y brindando una rejugabilidad ilimitada. Cada país se rige por su economía y potencial canterano."
                )

                HelpItemCard(
                    title = "⚽ Motor de Partido",
                    content = "La simulación ocurre minuto a minuto basada en la comparación táctica de tres zonas del campo: Defensa, Mediocampo y Ataque. Los atributos y rasgos únicos de cada jugador alteran directamente las probabilidades de cada jugada crítica del partido."
                )

                HelpItemCard(
                    title = "🏆 Confederaciones de Fantasía",
                    content = "Los clubes compiten en ligas nacionales y copas internacionales organizadas por confederaciones ficticias: SUDAMBOL (América del Sur), EUROBOL (Europa) y NORAMBOL (Norteamérica), todas coordinadas por la oficina global FEDEBOL."
                )

                HelpItemCard(
                    title = "📱 Red Social Interna",
                    content = "Las decisiones de contratación, tácticas de juego, resultados y los egos de las superestrellas se reflejan inmediatamente en el Feed Social del juego. Revisa las tendencias para evaluar la paciencia y el ánimo de la directiva y los aficionados."
                )

                HelpItemCard(
                    title = "💾 Persistencia Local Segura",
                    content = "Toda la partida (tu perfil, monedas, clubes, plantilla, fecha del calendario y noticias) se guarda localmente en el dispositivo de manera asíncrona para que puedas reanudar tu carrera en cualquier momento."
                )
            }
        }
    }
}

@Composable
fun HelpItemCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
        border = BorderStroke(1.dp, DarkSteel),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = GlacierBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}
