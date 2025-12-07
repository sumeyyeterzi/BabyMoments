package com.sumeyyaterzi.babymoments.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sumeyyaterzi.babymoments.BabyProfileViewModel
import com.sumeyyaterzi.babymoments.MomentsViewModel
import com.sumeyyaterzi.babymoments.data.Gender
import com.sumeyyaterzi.babymoments.utils.GrowthCalculator
import com.sumeyyaterzi.babymoments.utils.WHODataParser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthScreen(
    navController: NavController,
    viewModel: MomentsViewModel,
    profileViewModel: BabyProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val moments = viewModel.momentsFlow.collectAsState().value
    val profile = profileViewModel.profile.collectAsState().value

    val customPrimary = Color(0xFFF1A3A7)
    val customLight = Color(0xFFFDDDDD)

    // WHO verilerini yükle
    val whoData = remember { WHODataParser.loadWHOData(context) }

    // Kilo ve boy verilerini topla
    val weightData = moments.mapNotNull {
        it.weight?.let { weight -> it.date to weight }
    }.sortedBy { it.first }

    val heightData = moments.mapNotNull {
        it.height?.let { height -> it.date to height }
    }.sortedBy { it.first }

    // Son anı için WHO değerlendirmesi
    val lastMoment = moments.firstOrNull()
    val whoAssessment = remember(lastMoment, profile) {
        if (lastMoment != null && profile != null && lastMoment.weight != null && lastMoment.height != null) {
            val ageInMonths = GrowthCalculator.calculateAgeInMonths(profile.birthDate)
            val gender = if (profile.gender == "MALE") Gender.MALE else Gender.FEMALE

            GrowthCalculator.assessGrowth(
                ageInMonths = ageInMonths,
                gender = gender,
                weightKg = lastMoment.weight,
                heightCm = lastMoment.height,
                whoData = whoData
            )
        } else null
    }

    // Milestone'lar
    val milestones = moments.filter { !it.category.isNullOrEmpty() }
        .sortedByDescending { it.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gelişim Takibi",
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // WHO Değerlendirme Kartı
            if (whoAssessment != null) {
                item {
                    WHOAssessmentCard(
                        assessment = whoAssessment,
                        customPrimary = customPrimary,
                        customLight = customLight
                    )
                }
            }

            // Özet Kartlar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Son Kilo",
                        value = weightData.lastOrNull()?.second?.let { "${String.format("%.1f", it)} kg" } ?: "---",
                        icon = Icons.Default.Star,
                        color = customLight,
                        textColor = customPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    StatCard(
                        title = "Son Boy",
                        value = heightData.lastOrNull()?.second?.let { "${String.format("%.1f", it)} cm" } ?: "---",
                        icon = Icons.Default.Star,
                        color = customLight,
                        textColor = customPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Toplam Anı
            item {
                StatCard(
                    title = "Toplam Anı",
                    value = "${moments.size}",
                    icon = Icons.Default.Favorite,
                    color = customPrimary.copy(alpha = 0.1f),
                    textColor = customPrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Milestone'lar Başlığı
            if (milestones.isNotEmpty()) {
                item {
                    Text(
                        "Önemli Anlar",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = customPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Milestone'lar
            items(milestones) { moment ->
                MilestoneCard(
                    title = moment.title,
                    category = moment.category ?: "",
                    customLight = customLight,
                    customPrimary = customPrimary
                )
            }
        }
    }
}

@Composable
fun WHOAssessmentCard(
    assessment: com.sumeyyaterzi.babymoments.data.GrowthAssessment,
    customPrimary: Color,
    customLight: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = customPrimary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = customPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "WHO Değerlendirmesi",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = customPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Kilo Durumu
            AssessmentRow(
                label = "Kilo",
                value = assessment.weightPercentile,
                customLight = customLight,
                customPrimary = customPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Boy Durumu
            AssessmentRow(
                label = "Boy",
                value = assessment.heightPercentile,
                customLight = customLight,
                customPrimary = customPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tavsiye
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = customLight
            ) {
                Text(
                    text = assessment.advice,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = customPrimary
                )
            }
        }
    }
}

@Composable
fun AssessmentRow(
    label: String,
    value: String,
    customLight: Color,
    customPrimary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = customPrimary.copy(alpha = 0.7f)
        )
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = customLight
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = customPrimary
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = textColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun MilestoneCard(
    title: String,
    category: String,
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
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = customPrimary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = customPrimary.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = customPrimary
                )
            }
        }
    }
}