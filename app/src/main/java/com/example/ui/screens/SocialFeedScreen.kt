package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SocialPost
import com.example.ui.theme.*

@Composable
fun SocialFeedScreen(
    posts: List<SocialPost>,
    onDecisionTaken: (String, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Text("FAFI-NET MICROBLOGGING", color = GrassEmerald, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text("Reacciones de la hinchada, filtraciones de prensa y egos en tiempo real.", color = TextSecondary, fontSize = 11.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(posts, key = { it.id }) { post ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SurfaceCarbon),
                    border = BorderStroke(1.dp, if (post.isDecisionTrigger) GrassEmerald else DarkSteel),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row {
                                Text(post.authorName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(post.handle, color = GrassEmerald, fontSize = 11.sp)
                            }
                            Text(post.timeAgo, color = TextSecondary, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(post.content, color = TextPrimary, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("❤️ ${post.likes} Likes", color = TextSecondary, fontSize = 10.sp)
                            Text("🔁 ${post.reposts} Reposts", color = TextSecondary, fontSize = 10.sp)
                        }

                        // Decision trigger widget for crises management
                        if (post.isDecisionTrigger) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = PitchDarkBg),
                                border = BorderStroke(1.dp, NeonAmber),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("RESOLVER CRISIS: TOMA DE DECISIÓN DEL MÁNAGER", color = NeonAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    post.decisionOptions.forEachIndexed { idx, option ->
                                        Button(
                                            onClick = { onDecisionTaken(post.id, idx) },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkSteel, contentColor = TextPrimary),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 6.dp)
                                                .testTag("decision_btn_${idx}")
                                        ) {
                                            Text(option, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
