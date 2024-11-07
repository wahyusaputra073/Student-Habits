package com.wahyusembiring.data.repository.implementation

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.wahyusembiring.data.model.User
import com.wahyusembiring.data.repository.AuthRepository
import com.wahyusembiring.data.util.toUser
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.wahyusembiring.data.Result

class AuthRepositoryImpl @Inject constructor(
    private val application: Application
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
        private const val WEB_CLIENT_ID =
            "263007498597-j1ugeprd1k4t3cvt3dc51emporv9213b.apps.googleusercontent.com"
    }

    private val credentialManager by lazy {
        CredentialManager.create(application.applicationContext)
    }

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            trySend(it.currentUser?.toUser())
        }
        Firebase.auth.addAuthStateListener(listener)
        awaitClose { Firebase.auth.removeAuthStateListener(listener) }
    }

    override fun logout(): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            try {
                Firebase.auth.signOut()
                emit(Result.Success(Unit))
            } catch (throwable: Throwable) {
                emit(Result.Error(throwable))
            }
        }
    }

    override fun createUserWithEmailAndPassword(
        email: String,
        password: String
    ): Flow<Result<User>> {
        return flow {
            emit(Result.Loading())
            try {
                val result = Firebase.auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.let {
                    emit(Result.Success(it.toUser()))
                } ?: run {
                    emit(Result.Error(NullPointerException("Trying to create user with email and password but null user is returned")))
                }
            } catch (throwable: Throwable) {
                emit(Result.Error(throwable))
            }
        }
    }

    override fun signInWithFacebook(activityResultRegistryOwner: ActivityResultRegistryOwner): Flow<Result<User>> {
        val callbackManager = CallbackManager.Factory.create()
        val loginManager = LoginManager.getInstance()
        val permissions = listOf("email", "public_profile")

        return callbackFlow {
            trySend(Result.Loading())

            try {
                loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        trySend(Result.Error(Exception("Facebook login canceled")))
                    }

                    override fun onError(error: FacebookException) {
                        trySend(Result.Error(error))
                    }

                    override fun onSuccess(result: LoginResult) {
                        Log.d(TAG, "onSuccess: ${result.accessToken.token}")
                        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                        Firebase.auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = task.result.user ?: throw NullPointerException("Trying to sign in with Facebook but user is null")
                                    trySend(Result.Success(user.toUser()))
                                } else {
                                    trySend(Result.Error(task.exception ?: Exception("Facebook login failed")))
                                }
                            }
                    }
                })
                loginManager.logInWithReadPermissions(
                    activityResultRegistryOwner,
                    callbackManager,
                    permissions
                )
            } catch (exception: Exception) {
                trySend(Result.Error(exception))
            }

            awaitClose {
                loginManager.unregisterCallback(callbackManager)
            }
        }
    }

    override fun signInAnonymously(): Flow<Result<User>> {
        return flow {
            emit(Result.Loading())
            try {
                val result = Firebase.auth.signInAnonymously().await()
                result.user?.let {
                    emit(Result.Success(it.toUser()))
                } ?: run {
                    emit(Result.Error(NullPointerException("Trying to sign in anonymously but user is null")))
                }
            } catch (throwable: Throwable) {
                emit(Result.Error(throwable))
            }
        }
    }

    override fun signInWithEmailAndPassword(email: String, password: String): Flow<Result<User>> {
        return flow {
            emit(Result.Loading())
            try {
                val result = Firebase.auth.signInWithEmailAndPassword(email, password).await()
                result.user?.let {
                    emit(Result.Success(it.toUser()))
                } ?: run {
                    emit(Result.Error(NullPointerException("Trying to sign in with email and password but user is null")))
                }
            } catch (throwable: Throwable) {
                emit(Result.Error(throwable))
            }
        }
    }

    override fun signInWithGoogle(
        context: Context
    ): Flow<Result<User>> {
        return flow {
            emit(Result.Loading())
            try {
                val googleIdOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result = credentialManager.getCredential(
                    context = context,
                    request = request
                )
                val credential = result.credential
                if (
                    credential is CustomCredential
                    && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential = GoogleIdTokenCredential
                        .createFrom(credential.data)
                    val idToken = googleIdTokenCredential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val signInResult =
                        Firebase.auth.signInWithCredential(firebaseCredential).await()
                    signInResult.user?.let {
                        emit(Result.Success(it.toUser()))
                    } ?: run {
                        emit(Result.Error(NullPointerException("Trying to sign in with Google but user is null")))
                    }
                } else {
                    emit(Result.Error(Exception("Unexpected type of Credential, type not supported")))
                }
            } catch (exception: GetCredentialException) {
                emit(Result.Error(exception))
            } catch (throwable: Throwable) {
                emit(Result.Error(throwable))
            }
        }
    }
}