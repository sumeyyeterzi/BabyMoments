package com.sumeyyaterzi.babymoments.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.sumeyyaterzi.babymoments.MomentsViewModel
import com.sumeyyaterzi.babymoments.utils.copyUriToInternalStorage
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMomentScreen(
    navController: NavController,
    viewModel: MomentsViewModel,
    momentId: Int
) {
    val context = LocalContext.current
    val moment = viewModel.getMomentById(momentId)

    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf(moment?.title ?: "") }
    var note by remember { mutableStateOf(moment?.note ?: "") }
    var weight by remember { mutableStateOf(moment?.weight?.toString() ?: "") }
    var height by remember { mutableStateOf(moment?.height?.toString() ?: "") }
    var category by remember { mutableStateOf(moment?.category ?: "") }
    var currentPhotoUri by remember { mutableStateOf(moment?.photoUri) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    val categories = listOf("İlk Adım", "İlk Diş", "Doğum Günü", "Diğer")

    // Özel renkler
    val customPrimary = Color(0xFFF1A3A7)
    val customLight = Color(0xFFFDDDDD)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedUri = uri
    }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Moment bulunamadıysa geri dön
    if (moment == null) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Anıyı Düzenle",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = customPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Geri",
                            tint = customPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (title.isNotEmpty()) {
                        scope.launch {
                            val photoPath = if (selectedUri != null) {
                                copyUriToInternalStorage(context, selectedUri)
                            } else {
                                currentPhotoUri
                            }

                            val updatedMoment = moment.copy(
                                title = title,
                                note = note,
                                weight = weight.toFloatOrNull(),
                                height = height.toFloatOrNull(),
                                photoUri = photoPath,
                                category = category.ifEmpty { null }
                            )
                            viewModel.updateMoment(updatedMoment)
                            navController.popBackStack()
                        }
                    }
                },
                containerColor = customPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Check, contentDescription = "Kaydet")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Güncelle")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Fotoğraf Seçimi
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { launcher.launch("image/*") },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = customLight.copy(alpha = 0.3f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Önce yeni seçilen göster, yoksa mevcut olanı
                    val displayUri = selectedUri ?: currentPhotoUri?.let {
                        if (it.startsWith("file://")) {
                            Uri.parse(it)
                        } else {
                            Uri.fromFile(File(it))
                        }
                    }

                    if (displayUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(displayUri),
                            contentDescription = "Fotoğraf",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = customPrimary.copy(alpha = 0.2f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Fotoğraf Ekle",
                                        modifier = Modifier.size(32.dp),
                                        tint = customPrimary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Fotoğraf Değiştir",
                                style = MaterialTheme.typography.titleMedium,
                                color = customPrimary
                            )
                            Text(
                                "Dokunarak yeni fotoğraf seçin",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }

            // Başlık
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Başlık *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = customPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = customPrimary,
                    cursorColor = customPrimary
                ),
                isError = title.isEmpty()
            )

            // Kategori Seçimi
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCategoryDialog = true },
                shape = RoundedCornerShape(16.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(customLight)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Kategori",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            category.ifEmpty { "Kategori seçin" },
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (category.isEmpty())
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            else
                                customPrimary
                        )
                    }
                }
            }

            // Not
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Not") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = customPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = customPrimary,
                    cursorColor = customPrimary
                )
            )

            // Kilo ve Boy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Kilo (kg)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = customPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = customPrimary,
                        cursorColor = customPrimary
                    )
                )

                OutlinedTextField(
                    value = height,
                    onValueChange = { height = it },
                    label = { Text("Boy (cm)") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = customPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedLabelColor = customPrimary,
                        cursorColor = customPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Kategori Seçim Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = {
                Text(
                    "Kategori Seçin",
                    color = customPrimary
                )
            },
            text = {
                Column {
                    categories.forEach { cat ->
                        TextButton(
                            onClick = {
                                category = cat
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = customPrimary
                            )
                        ) {
                            Text(
                                cat,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showCategoryDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customPrimary
                    )
                ) {
                    Text("İptal")
                }
            },
            containerColor = Color.White
        )
    }
}