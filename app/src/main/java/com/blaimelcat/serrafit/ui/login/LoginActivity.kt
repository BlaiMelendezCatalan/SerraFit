package com.blaimelcat.serrafit.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blaimelcat.serrafit.MainActivity
import com.blaimelcat.serrafit.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginEmail = binding.edittextEmailLogin
        val loginPassword = binding.edittextPasswordLogin
        val loginLoginButton = binding.buttonLoginLogin
        val loginRegisterButton = binding.buttonRegisterLogin
        val loginLoadingProgBar = binding.progbarLoadingLogin

        mAuth = FirebaseAuth.getInstance()

        loginLoginButton.setOnClickListener {
            val email = loginEmail.text.toString()
            val password = loginPassword.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                if (it.isSuccessful) {
                    loginLoadingProgBar.visibility = View.VISIBLE
                    inflateMainActivity(email)
                } else {
                    showErrorAlert()
                }
            }
        }

        loginRegisterButton.setOnClickListener {
            inflateCreateUserActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        val email = mAuth.currentUser?.email
        if (email != null) {
            inflateMainActivity(email)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    private fun inflateMainActivity(email: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(intent)
    }

    private fun inflateCreateUserActivity() {
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
    }

    private fun showErrorAlert() {
        AlertDialog.Builder(this)
            .setTitle("Login Error")
            .setMessage("There was an error during authentication")
            .setNeutralButton("OK", null).show()
    }
}