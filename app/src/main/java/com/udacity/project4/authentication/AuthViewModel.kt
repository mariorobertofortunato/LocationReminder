package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class AuthViewModel : ViewModel () {

    enum class AuthState { AUTHENTICATED, UNAUTHENTICATED}

    val authState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthState.AUTHENTICATED
        } else {
            AuthState.UNAUTHENTICATED
        }
    }


}


