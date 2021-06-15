package com.blaimelcat.serrafit.ui.login

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.blaimelcat.serrafit.databinding.ActivityCreateUserBinding
import com.google.firebase.auth.FirebaseAuth

private lateinit var binding: ActivityCreateUserBinding

class CreateUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val createUserUsername = binding.edittextUsernameCreateuser
        val createUserEmail = binding.edittextEmailCreateuser
        val createUserPassword = binding.edittextPasswordCreateuser
        val createUserButton = binding.buttonCreateuserCreateuser
        val createUserProgBar = binding.progbarLoadingCreateuser

        createUserButton.setOnClickListener {
            if (createUserEmail.text.isNotEmpty() && createUserPassword.text.isNotEmpty() &&
                                                            createUserUsername.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    createUserEmail.text.toString(),
                    createUserPassword.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful) {
                        createUserProgBar.visibility = View.VISIBLE
                        inflateLoginActivity()
                    } else {
                        showErrorAlert()
                    }
                }
            }
        }

    }

    private fun inflateLoginActivity() {
        AlertDialog.Builder(this)
            .setTitle("Signed up!")
            .setMessage("You successfully registered")
            .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }).show()
    }

    private fun showErrorAlert() {
        AlertDialog.Builder(this)
            .setTitle("Sign up error")
            .setMessage("Something went wrong during sign up.")
            .setNegativeButton("OK", DialogInterface.OnClickListener { _, _ ->
            }).show()
    }
}