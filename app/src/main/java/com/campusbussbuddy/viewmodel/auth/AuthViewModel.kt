package com.campusbussbuddy.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusbussbuddy.domain.model.AuthState
import com.campusbussbuddy.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    // DEV MODE: NO VALIDATION - Accept any input
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                val result = authRepository.signIn(email, password)
                
                result.onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Sign in failed"
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "An unexpected error occurred during sign in"
                )
            }
        }
    }
    
    // DEV MODE: NO VALIDATION - Accept any input
    fun signUp(email: String, password: String, name: String, collegeId: String, role: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                
                val result = authRepository.signUp(email, password, name, collegeId, role)
                
                result.onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Registration failed"
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "An unexpected error occurred during registration"
                )
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                val result = authRepository.signOut()
                
                result.onSuccess {
                    _authState.value = AuthState.Unauthenticated
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Sign out failed"
                    )
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(
                    e.message ?: "An unexpected error occurred during sign out"
                )
            }
        }
    }
    
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}