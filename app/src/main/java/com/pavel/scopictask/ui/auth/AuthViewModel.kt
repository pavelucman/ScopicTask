package com.pavel.scopictask.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavel.scopictask.data.network.model.Response
import com.pavel.scopictask.data.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val userAuthenticatedStatus get() = authRepository.userAuthenticatedStatus

    private val _userAuthorizedEmail = MutableSharedFlow<Response<Boolean>>()
    val userAuthorizedEmail = _userAuthorizedEmail.asSharedFlow()

    fun signUpUser(email: String, password: String) = viewModelScope.launch {
        _userAuthorizedEmail.emit(Response.Loading)
        _userAuthorizedEmail.emit(authRepository.signUpUser(email, password))
    }

    fun signInUser(email: String, password: String) = viewModelScope.launch {
        _userAuthorizedEmail.emit(Response.Loading)
        _userAuthorizedEmail.emit(authRepository.signInUser(email, password))
    }

}