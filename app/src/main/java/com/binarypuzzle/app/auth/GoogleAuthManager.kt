package com.binarypuzzle.app.auth

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class GoogleAuthManager(private val context: Context) {

    private val auth = FirebaseAuth.getInstance()

    private val googleSignInClient: GoogleSignInClient by lazy {
        // default_web_client_id is auto-generated from google-services.json by the plugin
        val clientId = context.getString(
            context.resources.getIdentifier(
                "default_web_client_id", "string", context.packageName
            )
        )
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .requestProfile()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val currentUser: FirebaseUser? get() = auth.currentUser

    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    suspend fun handleSignInResult(data: Intent?): Result<FirebaseUser> {
        return try {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data).await()
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            Result.success(requireNotNull(authResult.user))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        auth.signOut()
        try {
            googleSignInClient.signOut().await()
        } catch (_: Exception) {
            // Ignore sign-out errors
        }
    }
}
