package com.wahyusembiring.data.repository

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResultRegistryOwner
import com.wahyusembiring.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import com.wahyusembiring.data.Result

interface AuthRepository {

    val currentUser: Flow<User?>

    fun signInAnonymously(): Flow<Result<User>>

    fun signInWithEmailAndPassword(email: String, password: String): Flow<Result<User>>

    fun signInWithGoogle(context: Context): Flow<Result<User>>

    fun signInWithFacebook(activityResultRegistryOwner: ActivityResultRegistryOwner): Flow<Result<User>>

}