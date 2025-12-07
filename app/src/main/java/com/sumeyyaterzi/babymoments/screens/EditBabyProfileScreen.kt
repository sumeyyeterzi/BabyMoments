package com.sumeyyaterzi.babymoments.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.sumeyyaterzi.babymoments.data.BabyProfileEntity
import com.sumeyyaterzi.babymoments.utils.copyUriToInternalStorage
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBabyProfileScreen(
    navController: NavController,
    viewModel: BabyProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val profile = viewModel.profile.collectAsState().value

    var name by remember { mutableStateOf(profile?.name ?: "") }
    var selectedGender by remember { mutableStateOf(profile?.gender ?: "MALE") }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(profile?.birthDate ?: System.currentTimeMillis()) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var currentPhotoUri by remember { mutableStateOf(profile?.photoUri) }
    var showGenderDialog by remember { mutableStateOf(false) }

    val customPrimary = Color(0xFFF1A3A7)
    val customLight = Color(0xFFFDDDD)

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedUri = uri
    }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (profile == null) "Profil Oluştur" else "Profili Düzenle",
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
                    if (name.isNotEmpty()) {
                        scope.launch {
                            val photoPath = if (selectedUri != null) {
                                copyUriToInternalStorage(context, selectedUri)
                            } else {
                                currentPhotoUri
                            }

                            if (profile == null) {
                                // Yeni profil oluştur
                                val newProfile = BabyProfileEntity(
                                    name = name,
                                    birthDate = selectedDate,
                                    gender = selectedGender,
                                    photoUri = photoPath
                                )
                                viewModel.saveProfile(newProfile)
                            } else {
                                // Mevcut profili güncelle
                                val updatedProfile = profile.copy(
                                    name = name,
                                    birthDate = selectedDate,
                                    gender = selectedGender,
                                    photoUri = photoPath
                                )
                                viewModel.updateProfile(updatedProfile)
                            }
                            navController.popBackStack()
                        }
                    }
                },
                containerColor = customPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Check, contentDescription = "Kaydet")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Kaydet")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Profil Fotoğrafı
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
                    )
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                val displayUri = selectedUri ?: currentPhotoUri?.let {
                    if (it.startsWith("file://")) Uri.parse(it)
                    else Uri.parse("file://$it")
                }

                if (displayUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(displayUri),
                        contentDescription = "Profil Fotoğrafı",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Fotoğraf Ekle",
                        tint = customPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Text(
                "Fotoğraf eklemek için tıklayın",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // İsim
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Bebek İsmi *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = customPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = customPrimary,
                    cursorColor = customPrimary
                ),
                isError = name.isEmpty()
            )

            // Doğum Tarihi
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
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
                            "Doğum Tarihi",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            SimpleDateFormat("dd MMMM yyyy", Locale("tr")).format(Date(selectedDate)),
                            style = MaterialTheme.typography.bodyLarge,
                            color = customPrimary
                        )
                    }
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        tint = customPrimary
                    )
                }
            }

            // Cinsiyet
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showGenderDialog = true },
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
                            "Cinsiyet",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            if (selectedGender == "MALE") "Erkek" else "Kız",
                            style = MaterialTheme.typography.bodyLarge,
                            color = customPrimary
                        )
                    }
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = customPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Tarih Seçici
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = it
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customPrimary
                    )
                ) {
                    Text("Tamam")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = customPrimary
                    )
                ) {
                    Text("İptal")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = customPrimary,
                    todayContentColor = customPrimary,
                    todayDateBorderColor = customPrimary
                )
            )
        }
    }

    // Cinsiyet Seçici Dialog
    if (showGenderDialog) {
        AlertDialog(
            onDismissRequest = { showGenderDialog = false },
            title = {
                Text(
                    "Cinsiyet Seçin",
                    color = customPrimary
                )
            },
            text = {
                Column {
                    listOf("Erkek" to "MALE", "Kız" to "FEMALE").forEach { (label, value) ->
                        TextButton(
                            onClick = {
                                selectedGender = value
                                showGenderDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = customPrimary
                            )
                        ) {
                            Text(
                                label,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showGenderDialog = false },
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