package com.sumeyyaterzi.babymoments.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.sumeyyaterzi.babymoments.BabyProfileViewModel
import com.sumeyyaterzi.babymoments.MomentsViewModel
import com.sumeyyaterzi.babymoments.data.Gender
import com.sumeyyaterzi.babymoments.data.MomentEntity
import com.sumeyyaterzi.babymoments.utils.GrowthCalculator
import com.sumeyyaterzi.babymoments.utils.WHODataParser
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    navController: NavController,
    viewModel: MomentsViewModel,
    profileViewModel: BabyProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val moments = viewModel.momentsFlow.collectAsState().value
    val profile = profileViewModel.profile.collectAsState().value

    // Özel renkler
    val customPrimary = Color(0xFFF1A3A7)
    val customLight = Color(0xFFFDDDDD)

    // WHO verilerini yükle
    val whoData = remember { WHODataParser.loadWHOData(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Anılarım",
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("add") },
                containerColor = customPrimary,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Anı Ekle", tint = Color.White)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (moments.isEmpty()) {
                EmptyState(customPrimary)
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(moments) { item ->
                        ModernTimelineItem(
                            item = item,
                            profile = profile,
                            whoData = whoData,
                            customPrimary = customPrimary,
                            customLight = customLight,
                            onEdit = { momentId ->
                                navController.navigate("edit/$momentId")
                            },
                            onDelete = { moment ->
                                viewModel.deleteMoment(moment)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(customColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = customColor.copy(alpha = 0.3f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Henüz anı eklemediniz",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "İlk anınızı eklemek için + butonuna tıklayın",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun ModernTimelineItem(
    item: MomentEntity,
    profile: com.sumeyyaterzi.babymoments.data.BabyProfileEntity?,
    whoData: com.sumeyyaterzi.babymoments.data.WHOData,
    customPrimary: Color,
    customLight: Color,
    onEdit: (Long) -> Unit,
    onDelete: (MomentEntity) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // WHO Değerlendirmesi (kilo ve boy varsa)
    val whoAssessment = remember(item, profile) {
        if (profile != null && item.weight != null && item.height != null) {
            val ageInMonths = GrowthCalculator.calculateAgeInMonths(profile.birthDate)
            val gender = if (profile.gender == "MALE") Gender.MALE else Gender.FEMALE

            GrowthCalculator.assessGrowth(
                ageInMonths = ageInMonths,
                gender = gender,
                weightKg = item.weight,
                heightCm = item.height,
                whoData = whoData
            )
        } else null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fotoğraf
                if (!item.photoUri.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        customLight,
                                        customPrimary.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(item.photoUri),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
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
                            Icons.Default.Favorite,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = customPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Başlık ve Tarih
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = customPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = SimpleDateFormat("dd MMMM yyyy", Locale("tr")).format(Date(item.date)),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    // Kategori badge
                    if (!item.category.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = customLight
                        ) {
                            Text(
                                text = item.category,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = customPrimary
                            )
                        }
                    }
                }
            }

            // Genişletilmiş içerik
            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = customLight)
                Spacer(modifier = Modifier.height(16.dp))

                // WHO Değerlendirmesi
                if (whoAssessment != null) {
                    WHOAssessmentCompact(
                        assessment = whoAssessment,
                        customPrimary = customPrimary,
                        customLight = customLight
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Not
                if (!item.note.isNullOrEmpty()) {
                    Text(
                        text = item.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Kilo ve Boy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item.weight?.let {
                        InfoChip(
                            label = "Kilo",
                            value = "${String.format("%.1f", it)} kg",
                            modifier = Modifier.weight(1f),
                            customColor = customLight,
                            textColor = customPrimary
                        )
                    }
                    item.height?.let {
                        InfoChip(
                            label = "Boy",
                            value = "${String.format("%.1f", it)} cm",
                            modifier = Modifier.weight(1f),
                            customColor = customLight,
                            textColor = customPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Edit & Delete Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            item.id?.let { onEdit(it) }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = customPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(customPrimary)
                        )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Düzenle",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Düzenle")
                    }

                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE57373)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE57373))
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Sil",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Sil")
                    }
                }
            }
        }
    }

    // Silme Onay Dialogu
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Anıyı Sil",
                    color = customPrimary
                )
            },
            text = {
                Text("Bu anıyı silmek istediğinizden emin misiniz?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(item)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFE57373)
                    )
                ) {
                    Text("Sil")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customPrimary
                    )
                ) {
                    Text("İptal")
                }
            }
        )
    }
}

@Composable
private fun WHOAssessmentCompact(
    assessment: com.sumeyyaterzi.babymoments.data.GrowthAssessment,
    customPrimary: Color,
    customLight: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = customPrimary.copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = customPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "WHO Değerlendirmesi",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = customPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    assessment.advice,
                    style = MaterialTheme.typography.bodySmall,
                    color = customPrimary.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun InfoChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    customColor: Color,
    textColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = customColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textColor
            )
        }
    }
}