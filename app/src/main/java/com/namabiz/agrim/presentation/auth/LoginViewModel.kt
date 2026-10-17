package com.namabiz.agrim.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namabiz.agrim.core.datastore.UserPreferencesRepository
import com.namabiz.agrim.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPass: String) {
        _password.value = newPass
    }

    fun autofillDemoBuyer() {
        _email.value = "buyer@agrim.com"
        _password.value = "password"
    }

    fun login() {
        val currentEmail = _email.value.trim()
        val currentPass = _password.value

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex()
        if (currentEmail.isBlank() || !currentEmail.matches(emailRegex)) {
            _uiState.value = LoginUiState.Error("Please enter a valid business email address.")
            return
        }

        if (currentPass.length < 6) {
            _uiState.value = LoginUiState.Error("Password must be at least 6 characters.")
            return
        }

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val success = authRepository.login(currentEmail, currentPass)
            if (success) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Invalid email or password. Please check your credentials.")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
