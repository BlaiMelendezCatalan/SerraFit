package com.blaimelcat.serrafit.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blaimelcat.serrafit.MainActivity
import com.blaimelcat.serrafit.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

private lateinit var binding: ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginEmail = binding.edittextEmailLogin
        val loginPassword = binding.edittextPasswordLogin
        val loginLoginButton = binding.buttonLoginLogin
        val loginRegisterButton = binding.buttonRegisterLogin
        val loginLoadingProgBar = binding.progbarLoadingLogin

        loginLoginButton.setOnClickListener {
            if (loginEmail.text.isNotEmpty() && loginPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(loginEmail.text.toString(),
                    loginPassword.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        loginLoadingProgBar.visibility = View.VISIBLE
                        inflateMainActivity()
                    } else {
                        showErrorAlert()
                    }
                }
            }
        }

        loginRegisterButton.setOnClickListener {
            inflateCreateUserActivity()
        }
    }

    private fun inflateMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
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