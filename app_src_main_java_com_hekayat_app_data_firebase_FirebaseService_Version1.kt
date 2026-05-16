package com.hekayat.app.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.hekayat.app.data.models.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // ==================== Authentication ====================
    
    suspend fun signUpWithEmail(email: String, password: String): Result<String> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.uid?.let { uid ->
            // Create user document
            createUserDocument(uid, email)
            Result.success(uid)
        } ?: Result.failure(Exception("User creation failed"))
    } catch (e: Exception) {
        Log.e("FirebaseService", "SignUp error: ${e.message}")
        Result.failure(e)
    }

    suspend fun signInWithEmail(email: String, password: String): Result<String> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        Result.success(result.user?.uid ?: "")
    } catch (e: Exception) {
        Log.e("FirebaseService", "SignIn error: ${e.message}")
        Result.failure(e)
    }

    suspend fun signOut(): Result<Unit> = try {
        auth.signOut()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    fun getCurrentUser() = auth.currentUser

    // ==================== User Management ====================

    private suspend fun createUserDocument(uid: String, email: String) {
        val user = User(
            id = uid,
            email = email,
            displayName = email.substringBefore("@"),
            isAdmin = email == "mohashraf304@gmail.com"
        )
        firestore.collection("users").document(uid).set(user).await()
    }

    suspend fun updateUserProfile(
        userId: String,
        displayName: String,
        bio: String,
        imageUrl: String
    ): Result<Unit> = try {
        firestore.collection("users").document(userId).update(
            mapOf(
                "displayName" to displayName,
                "bio" to bio,
                "profileImageUrl" to imageUrl
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getUserData(userId: String): Result<User> = try {
        val document = firestore.collection("users").document(userId).get().await()
        val user = document.toObject(User::class.java) ?: User()
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun addCoins(userId: String, amount: Long): Result<Unit> = try {
        firestore.collection("users").document(userId).update(
            mapOf("coins" to com.google.firebase.firestore.FieldValue.increment(amount))
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deductCoins(userId: String, amount: Long): Result<Unit> = try {
        val user = getUserData(userId).getOrNull() ?: return Result.failure(Exception("User not found"))
        if (user.coins >= amount) {
            firestore.collection("users").document(userId).update(
                mapOf("coins" to com.google.firebase.firestore.FieldValue.increment(-amount))
            ).await()
            Result.success(Unit)
        } else {
            Result.failure(Exception("Insufficient coins"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== Novels ====================

    suspend fun addNovel(novel: Novel): Result<String> = try {
        val docRef = firestore.collection("novels").document()
        val novelWithId = novel.copy(id = docRef.id)
        docRef.set(novelWithId).await()
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getNovel(novelId: String): Result<Novel> = try {
        val document = firestore.collection("novels").document(novelId).get().await()
        val novel = document.toObject(Novel::class.java) ?: Novel()
        Result.success(novel)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getTrendingNovels(limit: Long = 10): Result<List<Novel>> = try {
        val query = firestore.collection("novels")
            .orderBy("views", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
        
        val novels = query.toObjects(Novel::class.java)
        Result.success(novels)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getNovelsByCategory(category: String, limit: Long = 10): Result<List<Novel>> = try {
        val query = firestore.collection("novels")
            .whereEqualTo("category", category)
            .orderBy("views", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
        
        val novels = query.toObjects(Novel::class.java)
        Result.success(novels)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateNovel(novelId: String, updates: Map<String, Any>): Result<Unit> = try {
        firestore.collection("novels").document(novelId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== Chapters ====================

    suspend fun addChapter(novelId: String, chapter: Chapter): Result<String> = try {
        val docRef = firestore.collection("novels").document(novelId)
            .collection("chapters").document()
        
        val chapterWithId = chapter.copy(
            id = docRef.id,
            novelId = novelId
        )
        docRef.set(chapterWithId).await()
        
        // Update novel total chapters count
        firestore.collection("novels").document(novelId).update(
            mapOf("totalChapters" to com.google.firebase.firestore.FieldValue.increment(1))
        ).await()
        
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getChapter(novelId: String, chapterId: String): Result<Chapter> = try {
        val document = firestore.collection("novels").document(novelId)
            .collection("chapters").document(chapterId).get().await()
        
        val chapter = document.toObject(Chapter::class.java) ?: Chapter()
        Result.success(chapter)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getChapters(novelId: String): Result<List<Chapter>> = try {
        val query = firestore.collection("novels").document(novelId)
            .collection("chapters")
            .orderBy("chapterNumber", Query.Direction.ASCENDING)
            .get()
            .await()
        
        val chapters = query.toObjects(Chapter::class.java)
        Result.success(chapters)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== Comments ====================

    suspend fun addComment(comment: Comment): Result<String> = try {
        val docRef = firestore.collection("comments").document()
        val commentWithId = comment.copy(id = docRef.id)
        docRef.set(commentWithId).await()
        
        // Update chapter comments count
        firestore.collection("novels").document(comment.novelId)
            .collection("chapters").document(comment.chapterId)
            .update("comments", com.google.firebase.firestore.FieldValue.increment(1)).await()
        
        Result.success(docRef.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun getComments(chapterId: String): Result<List<Comment>> = try {
        val query = firestore.collection("comments")
            .whereEqualTo("chapterId", chapterId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        
        val comments = query.toObjects(Comment::class.java)
        Result.success(comments)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== Search ====================

    suspend fun searchNovels(query: String): Result<List<Novel>> = try {
        val snapshot = firestore.collection("novels")
            .get()
            .await()
        
        val novels = snapshot.toObjects(Novel::class.java).filter { novel ->
            novel.title.contains(query, ignoreCase = true) ||
            novel.authorName.contains(query, ignoreCase = true) ||
            novel.tags.any { it.contains(query, ignoreCase = true) }
        }
        
        Result.success(novels)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ==================== Admin ====================

    suspend fun getUserStats(): Result<UserStats> = try {
        val usersCount = firestore.collection("users").get().await().size()
        val novelsSnapshot = firestore.collection("novels").get().await()
        
        val totalReads = novelsSnapshot.toObjects(Novel::class.java).sumOf { it.views }
        
        Result.success(UserStats(
            totalReads = totalReads,
            totalUsers = usersCount.toLong()
        ))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun approveNovel(novelId: String): Result<Unit> = try {
        firestore.collection("novels").document(novelId)
            .update("isApproved", true).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}