package com.vidyavahini.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    suspend fun signIn(email: String, pass: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, pass).await()
        return result.user
    }

    suspend fun signUp(email: String, pass: String): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, pass).await()
        return result.user
    }

    fun signOut() {
        auth.signOut()
    }
}
