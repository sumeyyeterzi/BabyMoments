package com.sumeyyaterzi.babymoments.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sumeyyaterzi.babymoments.BabyProfileViewModel
import com.sumeyyaterzi.babymoments.utils.GrowthCalculator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BabyProfileScreen(
    navController: NavController,
    viewModel: BabyProfileViewModel = hiltViewModel()
) {
    val profile = viewModel.profile.collectAsState().value
    val customPrimary = Color(0xFFF1A3A7)
    val customLight = Color(0xFFFDDDDD)

    // Profil verileri
    val babyName = profile?.name ?: "Bebeğimin Adı"

    val birthDate = profile?.birthDate?.let {
        SimpleDateFormat("dd MMMM yyyy", Locale("tr")).format(Date(it))
    } ?: "Belirtilmedi"

    val age = profile?.birthDate?.let {
        GrowthCalculator.calculateAgeInMonths(it)
    }

    val ageText = age?.let {
        val years = it / 12
        val months = it % 12
        when {
            years > 0 && months > 0 -> "$years Yıl $months Ay"
            years > 0 -> "$years Yıl"
            else -> "$months Ay"
        }
    } ?: "Belirtilmedi"

    val gender = profile?.gender?.let {
        if (it == "MALE") "Erkek" else "Kız"
    } ?: "Belirtilmedi"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Bebek Profili",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = customPrimary
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Profil Fotoğrafı Placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                customLight,
                                customPrimary.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Bebek Fotoğrafı",
                    tint = customPrimary,
                    modifier = Modifier.size(60.dp)
                )
            }

            // İsim
            Text(
                text = babyName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = customPrimary
            )

            // Bilgi Kartları
            InfoCard(
                title = "Doğum Tarihi",
                value = birthDate,
                icon = Icons.Default.DateRange,
                customLight = customLight,
                customPrimary = customPrimary
            )

            InfoCard(
                title = "Yaş",
                value = ageText,
                icon = Icons.Default.AccountBox,
                customLight = customLight,
                customPrimary = customPrimary
            )

            InfoCard(
                title = "Cinsiyet",
                value = gender,
                icon = Icons.Default.Person,
                customLight = customLight,
                customPrimary = customPrimary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Düzenle Butonu
            // Düzenle Butonu
            Button(
                onClick = { navController.navigate("edit_profile") },  // ← Güncelle
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = customPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Profili Düzenle")
            }

            // Ayarlar Butonu
            OutlinedButton(
                onClick = { /* TODO: Ayarlar */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = customPrimary
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(customPrimary)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ayarlar")
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    customLight: Color,
    customPrimary: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = customLight.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = customPrimary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = customPrimary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = customPrimary
                )
            }
        }
    }
}