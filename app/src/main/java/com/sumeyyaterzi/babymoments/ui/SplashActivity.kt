package com.sumeyyaterzi.babymoments.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.airbnb.lottie.compose.*
import com.sumeyyaterzi.babymoments.MainActivity
import com.sumeyyaterzi.babymoments.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
@AndroidEntryPoint
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Eğer Lottie animasyonun varsa true yap, yoksa false
            val useLottie = false

            if (useLottie) {
                LottieSplashScreen {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            } else {
                ImageSplashScreen {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    // -----------------------------
    // Lottie JSON Animasyon Splash
    // -----------------------------
    @Composable
    fun LottieSplashScreen(onSplashFinished: () -> Unit) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = 1, // sadece bir kez oynat
            isPlaying = true
        )

        // Animasyon bittiğinde MainActivity'ye geç
        LaunchedEffect(progress) {
            if (progress == 1f) {
                onSplashFinished()
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
            )
        }
    }

    // -----------------------------
    // PNG/JPG Görsel Splash
    // -----------------------------
    @Composable
    fun ImageSplashScreen(onSplashFinished: () -> Unit) {
        LaunchedEffect(true) {
            delay(2500) // Splash ekran süresi
            onSplashFinished()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.splash_image), // drawable içindeki PNG/JPG
                contentDescription = "Splash",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
