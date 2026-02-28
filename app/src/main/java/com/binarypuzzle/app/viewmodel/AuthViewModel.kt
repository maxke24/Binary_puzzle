package com.binarypuzzle.app.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.binarypuzzle.app.auth.GoogleAuthManager
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authManager: GoogleAuthManager) : ViewModel() {

    private val _user = MutableStateFlow<FirebaseUser?>(authManager.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun getSignInIntent(): Intent = authManager.getSignInIntent()

    fun handleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            authManager.handleSignInResult(data).fold(
                onSuccess = { user -> _user.value = user },
                onFailure = { e -> _error.value = e.message ?: "Sign-in failed" }
            )
            _isLoading.value = false
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authManager.signOut()
            _user.value = null
        }
    }

    fun clearError() {
        _error.value = null
    }
}
