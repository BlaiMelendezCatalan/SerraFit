package com.blaimelcat.serrafit.ui.login

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.blaimelcat.serrafit.R
import com.blaimelcat.serrafit.databinding.ActivityCreateUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Matcher
import java.util.regex.Pattern


class CreateUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = binding.edittextUsernameCreateuser
        val email = binding.edittextEmailCreateuser
        val password = binding.edittextPasswordCreateuser
        val password2 = binding.edittextPassword2Createuser
        val button = binding.buttonCreateuserCreateuser
        val progressBar = binding.progbarLoadingCreateuser

        val db = FirebaseFirestore.getInstance()

        button.setOnClickListener {
            if (checkUsername(username.text.toString()) && checkEmail(email.text.toString())
                && checkPassword(password.text.toString(), password2.text.toString())){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    email.text.toString(), password.text.toString())
                    .addOnCompleteListener {
                    if (it.isSuccessful) {
                        progressBar.visibility = View.VISIBLE
                        db.collection("users").document(email.text.toString()).set(
                            hashMapOf("username" to username.text.toString(), "admin" to false)
                        )
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

    private fun checkUsername(username: String): Boolean {
        val p: Pattern = Pattern.compile(getString(R.string.regex_str), Pattern.CASE_INSENSITIVE)
        val m: Matcher = p.matcher(username)
        return if (username != "" && !m.find()) {
            true
        } else {
            showUsernameErrorAlert()
            false
        }
    }

    private fun showUsernameErrorAlert() {
        AlertDialog.Builder(this)
            .setTitle("Sign up error")
            .setMessage("Username must contain only letters and numbers")
            .setNegativeButton("OK", DialogInterface.OnClickListener { _, _ ->
            }).show()
    }

    private fun checkEmail(email: String): Boolean {
        return if (email != "" && email.contains("@")) {
            true
        } else {
            showEmailErrorAlert()
            false
        }
    }

    private fun showEmailErrorAlert() {
        AlertDialog.Builder(this)
            .setTitle("Sign up error")
            .setMessage("Invalid email address")
            .setNegativeButton("OK", DialogInterface.OnClickListener { _, _ ->
            }).show()
    }

    private fun checkPassword(password: String, password2: String): Boolean {
        if (password != password2) {
            showPasswordMismatch()
            return false
        }
        val p: Pattern = Pattern.compile(getString(R.string.regex_str), Pattern.CASE_INSENSITIVE)
        val m: Matcher = p.matcher(password)
        return if (password.length >= 8 && !m.find()) {
            true
        } else {
            showPasswordErrorAlert()
            false
        }
    }

    private fun showPasswordMismatch() {
        AlertDialog.Builder(this)
            .setTitle("Sign up error")
            .setMessage("Passwords do not match")
            .setNegativeButton("OK", DialogInterface.OnClickListener { _, _ ->
            }).show()
    }

    private fun showPasswordErrorAlert() {
        AlertDialog.Builder(this)
            .setTitle("Sign up error")
            .setMessage("Password must be at least 8 characters long and must contain only letters and numbers")
            .setNegativeButton("OK", DialogInterface.OnClickListener { _, _ ->
            }).show()
    }

}