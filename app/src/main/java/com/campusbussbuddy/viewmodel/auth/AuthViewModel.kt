package com.campusbussbuddy.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.campusbussbuddy.domain.model.AuthState
import com.campusbussbuddy.domain.model.User
import com.campusbussbuddy.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        observeAuthState()
    }
    
    private fun observeAuthState() {
        viewModelScope.launch {
            authRepository.getCurrentUser()
                .collect { user ->
                    _authState.value = if (user != null) {
                        AuthState.Authenticated(user)
                    } else {
                        AuthState.Unauthenticated
                    }
                }
        }
    }
    
    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            authRepository.signIn(email, password)
                .onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Sign in failed"
                    )
                }
        }
    }
    
    fun signUp(email: String, password: String, name: String, collegeId: String, role: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            authRepository.signUp(email, password, name, collegeId, role)
                .onSuccess { user ->
                    _authState.value = AuthState.Authenticated(user)
                }
                .onFailure { exception ->
                    _authState.value = AuthState.Error(
                        exception.message ?: "Registration failed"
                    )
                }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
                .onSuccess {
                    _authState.value = AuthState.Unauthenticated
                }
        }
    }
    
    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Unauthenticated
        }
    }
}