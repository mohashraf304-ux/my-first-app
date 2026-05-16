package com.hekayat.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hekayat.app.ui.screens.*
import com.hekayat.app.ui.theme.HekayatTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            HekayatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    
                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(navController)
                        }
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("signup") {
                            SignupScreen(navController)
                        }
                        composable("home") {
                            HomeScreen(navController)
                        }
                        composable("novel/{novelId}") { backStackEntry ->
                            val novelId = backStackEntry.arguments?.getString("novelId") ?: ""
                            NovelDetailScreen(navController, novelId)
                        }
                        composable("reader/{novelId}/{chapterId}") { backStackEntry ->
                            val novelId = backStackEntry.arguments?.getString("novelId") ?: ""
                            val chapterId = backStackEntry.arguments?.getString("chapterId") ?: ""
                            ReaderScreen(navController, novelId, chapterId)
                        }
                        composable("profile") {
                            ProfileScreen(navController)
                        }
                        composable("admin") {
                            AdminPanelScreen(navController)
                        }
                    }
                }
            }
        }
    }
}