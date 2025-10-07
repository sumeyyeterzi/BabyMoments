package com.sumeyyaterzi.babymoments.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.sumeyyaterzi.babymoments.MomentsViewModel
import com.sumeyyaterzi.babymoments.data.MomentEntity
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TimelineScreen(navController: NavController, viewModel: MomentsViewModel) {
    val moments = viewModel.momentsFlow.collectAsState().value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) { Text("+") }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(moments) { item ->
                TimelineItem(item)
            }
        }
    }
}

@Composable
fun TimelineItem(item: MomentEntity) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        if (!item.photoUri.isNullOrEmpty()) {
            Image(
                painter = rememberAsyncImagePainter(item.photoUri),
                contentDescription = null,
                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp))
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(item.title, style = MaterialTheme.typography.titleMedium)
            Text(SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(item.date)))
            if (!item.note.isNullOrEmpty()) Text(item.note ?: "", maxLines = 2)
        }
    }
}
