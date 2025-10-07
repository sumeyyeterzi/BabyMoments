package com.sumeyyaterzi.babymoments.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.sumeyyaterzi.babymoments.MomentsViewModel
import com.sumeyyaterzi.babymoments.data.MomentEntity
import com.sumeyyaterzi.babymoments.utils.copyUriToInternalStorage
import kotlinx.coroutines.launch

@Composable
fun AddMomentScreen(
    navController: NavController,
    viewModel: MomentsViewModel
) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedUri = uri
    }

    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TextField(value = title, onValueChange = { title = it }, label = { Text("Başlık") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = note, onValueChange = { note = it }, label = { Text("Not") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = weight, onValueChange = { weight = it }, label = { Text("Kilo (kg)") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = height, onValueChange = { height = it }, label = { Text("Boy (cm)") })
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = category, onValueChange = { category = it }, label = { Text("Kategori") })
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { launcher.launch("image/*") }) { Text("Fotoğraf seç") }

        selectedUri?.let {
            Image(
                painter = rememberAsyncImagePainter(it),
                contentDescription = null,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val savedPath = copyUriToInternalStorage(context, selectedUri)
                val moment = MomentEntity(
                    title = title,
                    note = note,
                    date = System.currentTimeMillis(),
                    weight = weight.toFloatOrNull(),
                    height = height.toFloatOrNull(),
                    photoUri = savedPath,
                    category = category
                )
                viewModel.addMoment(moment)
                navController.popBackStack()
            }
        }) { Text("Kaydet") }
    }
}
