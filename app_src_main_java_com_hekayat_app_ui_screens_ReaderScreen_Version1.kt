package com.hekayat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hekayat.app.data.firebase.FirebaseService
import com.hekayat.app.data.models.Chapter
import kotlinx.coroutines.launch

@Composable
fun ReaderScreen(navController: NavController, novelId: String, chapterId: String) {
    val firebaseService = FirebaseService()
    var chapter by remember { mutableStateOf<Chapter?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showMenu by remember { mutableStateOf(false) }
    var fontSize by remember { mutableStateOf(16.sp) }
    var isNightMode by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(chapterId) {
        scope.launch {
            try {
                firebaseService.getChapter(novelId, chapterId).onSuccess {
                    chapter = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
        }
    } else if (chapter != null) {
        val bgColor = if (isNightMode) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.background
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgColor)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        if (offset.x < size.width / 3) {
                            // Left tap - previous chapter
                        } else if (offset.x > (size.width * 2 / 3)) {
                            // Right tap - next chapter
                        } else {
                            // Center tap - toggle menu
                            showMenu = !showMenu
                        }
                    }
                }
        ) {
            // Reader Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = chapter!!.title,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = chapter!!.content,
                    fontSize = fontSize,
                    lineHeight = (fontSize.value * 1.8f).sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Top App Bar
            if (showMenu) {
                TopAppBar(
                    title = { Text(chapter!!.title) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    },
                    actions = {
                        IconButton(onClick = { isNightMode = !isNightMode }) {
                            Icon(
                                if (isNightMode) Icons.Default.LightMode else Icons.Default.Brightness4,
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }

            // Bottom Reader Controls
            if (showMenu) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    // Font Size Control
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("حجم الخط")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { if (fontSize > 12.sp) fontSize -= 2.sp }) {
                                Icon(Icons.Default.Remove, contentDescription = null)
                            }
                            Text(
                                text = "${fontSize.value.toInt()}",
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            IconButton(onClick = { if (fontSize < 24.sp) fontSize += 2.sp }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                            }
                        }
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(Icons.Default.Comment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تعليقات")
                        }

                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("شارك")
                        }
                    }
                }
            }
        }
    }
}