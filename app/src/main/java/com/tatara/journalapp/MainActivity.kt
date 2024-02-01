package com.tatara.journalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.tatara.journalapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.signUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.signInButton.setOnClickListener {
            loginWithEmailPassword(
                binding.emailText.text.toString().trim(),
                binding.passwordText.text.toString().trim()
            )
        }

        if (!::auth.isInitialized) {
            auth = Firebase.auth
        }
    }

    private fun loginWithEmailPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val journal: JournalUser = JournalUser.instance!!
                    journal.userId = auth.currentUser?.uid
                    journal.username = auth.currentUser?.displayName
                    goToJournalList()
//                    updateUI(user)
                } else {
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
//                    updateUI(null)
                }
            }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            goToJournalList()
        }
    }

    private fun goToJournalList() {
        val i = Intent(this, JournalList::class.java)
        startActivity(i)
    }
}