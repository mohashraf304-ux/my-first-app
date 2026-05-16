package com.hekayat.app.data.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val profileImageUrl: String = "",
    val bio: String = "",
    val coins: Long = 0L,
    val gems: Long = 0L,
    val readingPoints: Long = 0L,
    val novelsRead: Long = 0L,
    val totalReadingTime: Long = 0L,
    val isAdmin: Boolean = false,
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val premiumExpireDate: Date? = null,
    val favoriteNovels: List<String> = emptyList(),
    val readingHistory: List<String> = emptyList(),
    val language: String = "ar",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val lastLogin: Date? = null
)

data class UserStats(
    val totalReads: Long = 0L,
    val totalCoins: Long = 0L,
    val onlineUsers: Long = 0L,
    val totalUsers: Long = 0L,
    val dailyRevenue: Double = 0.0
)