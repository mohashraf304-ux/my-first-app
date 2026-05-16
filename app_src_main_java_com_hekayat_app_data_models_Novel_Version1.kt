package com.hekayat.app.data.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Novel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val coverImageUrl: String = "",
    val category: String = "",
    val rating: Float = 0f,
    val ratingCount: Long = 0L,
    val views: Long = 0L,
    val likes: Long = 0L,
    val comments: Long = 0L,
    val isCompleted: Boolean = false,
    val isFeatured: Boolean = false,
    val isPremium: Boolean = false,
    val freeChapters: Int = 10,
    val totalChapters: Int = 0,
    val tags: List<String> = emptyList(),
    val language: String = "ar",
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)

data class Chapter(
    val id: String = "",
    val novelId: String = "",
    val title: String = "",
    val content: String = "",
    val chapterNumber: Int = 0,
    val isPremium: Boolean = false,
    val views: Long = 0L,
    val likes: Long = 0L,
    val comments: Long = 0L,
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
)

data class Comment(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "",
    val chapterId: String = "",
    val novelId: String = "",
    val content: String = "",
    val likes: Long = 0L,
    val replies: List<Reply> = emptyList(),
    @ServerTimestamp
    val createdAt: Date? = null
)

data class Reply(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val content: String = "",
    val likes: Long = 0L,
    @ServerTimestamp
    val createdAt: Date? = null
)