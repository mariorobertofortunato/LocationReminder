package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.locationreminders.RemindersActivity
import kotlinx.android.synthetic.main.activity_authentication.*

/**
 * This class should be the starting point of the app,
 * It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    private val viewModel by viewModels<AuthViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        observeAuthState()

    }

    /**Check if user is logged and change the UI accordingly*/
    private fun observeAuthState() {

        viewModel.authState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                AuthViewModel.AuthState.AUTHENTICATED -> {
                    /**If user logged in, the button navs to RemindersActivity*/
                    logoutBtn.isVisible = true
                    logoutBtn.text = "LOGOUT"
                    text.text = "Welcome ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                    loginBtn.text = "START"
                    logoutBtn.setOnClickListener { AuthUI.getInstance().signOut(this) }
                    loginBtn.setOnClickListener {
                        val intent = Intent(this@AuthenticationActivity, RemindersActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                else -> {
                    /**Else start the signInFlow*/
                    logoutBtn.isVisible = false
                    text.text = getString(R.string.welcome_to_the_location_reminder_app)
                    loginBtn.text = getString(R.string.login)
                    loginBtn.setOnClickListener {
                        signInFlow()
                    }
                }
            }
        })

    }

    private fun signInFlow() {
        // Give users the option to sign in / register with their email or Google account. If users
        // choose to register with their email, they will need to create a password as well.
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        // Create and launch sign-in intent. We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code.
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).build(), SIGN_IN_RESULT_CODE
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(applicationContext,"Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext,"Sign in unsuccessful ${response?.error?.errorCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val  SIGN_IN_RESULT_CODE = 1001
    }

}







