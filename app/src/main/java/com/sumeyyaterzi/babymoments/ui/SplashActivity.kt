package com.sumeyyaterzi.babymoments.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sumeyyaterzi.babymoments.MainActivity
import com.sumeyyaterzi.babymoments.ui.theme.BabyMomentsTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BabyMomentsTheme {
                ModernSplashScreen {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }


    //deneme deneme deemm

    @Composable
    fun ModernSplashScreen(onSplashFinished: () -> Unit) {
        // Animasyonlar
        val infiniteTransition = rememberInfiniteTransition(label = "splash")

        // Kalp atışı animasyonu
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "heartbeat"
        )

        // Fade-in animasyonu
        val alpha by animateFloatAsState(
            targetValue = 1f,
            animationSpec = tween(1000),
            label = "fade"
        )

        LaunchedEffect(true) {
            delay(2500) // 2.5 saniye splash
            onSplashFinished()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF5F7), // Çok açık pembe
                            Color(0xFFFFF8FA), // Neredeyse beyaz pembe
                            Color(0xFFFFFFFF)  // Beyaz
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Kalp İkonu (animasyonlu)
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scale),
                    tint = Color(0xFFFDDDDD) // Özel açık pembe
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Uygulama Adı
                Text(
                    text = "Baby Moments",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF1A3A7) // Özel koyu pembe
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Alt Yazı
                Text(
                    text = "Her anı değerli",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    color = Color(0xFFF1A3A7).copy(alpha = 0.7f)
                )
            }
        }
    }
}