package com.hekayat.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.hekayat.app.data.firebase.FirebaseService
import com.hekayat.app.data.models.User
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val firebaseService = FirebaseService()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    var userData by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUser?.uid) {
        scope.launch {
            currentUser?.uid?.let { userId ->
                firebaseService.getUserData(userId).onSuccess {
                    userData = it
                }
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الملف الشخصي") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
            }
        } else if (userData != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                // Profile Header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Profile Image
                        if (userData!!.profileImageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = userData!!.profileImageUrl,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary)
                                    .padding(24.dp),
                                tint = MaterialTheme.colorScheme.onSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userData!!.displayName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        Text(
                            text = userData!!.email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )

                        if (userData!!.isPremium) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier.clip(CircleShape),
                                color = MaterialTheme.colorScheme.secondary
                            ) {
                                Text(
                                    text = "Premium",
                                    modifier = Modifier.padding(8.dp, 4.dp),
                                    color = MaterialTheme.colorScheme.onSecondary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }

                // Stats
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard(
                            icon = Icons.Default.Visibility,
                            label = "الروايات المقروءة",
                            value = userData!!.novelsRead.toString()
                        )
                        StatCard(
                            icon = Icons.Default.Star,
                            label = "النقاط",
                            value = userData!!.readingPoints.toString()
                        )
                        StatCard(
                            icon = Icons.Default.AttachMoney,
                            label = "العملات",
                            value = userData!!.coins.toString()
                        )
                    }
                }

                // Menu Items
                item {
                    Text(
                        text = "الإعدادات",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                item {
                    MenuItemCard(
                        icon = Icons.Default.Edit,
                        title = "تعديل الملف الشخصي",
                        onClick = { }
                    )
                }

                item {
                    MenuItemCard(
                        icon = Icons.Default.ShoppingCart,
                        title = "اشتراكات وعملات",
                        onClick = { }
                    )
                }

                item {
                    MenuItemCard(
                        icon = Icons.Default.Notifications,
                        title = "الإشعارات",
                        onClick = { }
                    )
                }

                item {
                    MenuItemCard(
                        icon = Icons.Default.Settings,
                        title = "الإعدادات",
                        onClick = { }
                    )
                }

                item {
                    MenuItemCard(
                        icon = Icons.Default.Help,
                        title = "مساعدة وملاحظات",
                        onClick = { }
                    )
                }

                item {
                    Button(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo("profile") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("تسجيل الخروج")
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    icon: androidx.compose.material.icons.Icons.Filled,
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontSize = 9.sp
        )
    }
}

@Composable
fun MenuItemCard(
    icon: androidx.compose.material.icons.Icons.Filled,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.surface,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}