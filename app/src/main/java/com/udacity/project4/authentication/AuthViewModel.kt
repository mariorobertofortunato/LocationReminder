package com.udacity.project4.authentication

import android.util.Log
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

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


