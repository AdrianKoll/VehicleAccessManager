package com.aplicativo_text_10.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.aplicativo_text_10.R

class SignInActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var btnEntrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        etEmail = findViewById(R.id.editTextEmail)
        etSenha = findViewById(R.id.editTextPassword)
        btnEntrar = findViewById(R.id.buttonSignIn)

        btnEntrar.setOnClickListener { performLogin() }
    }

    private fun performLogin() {
        val email = etEmail.text.toString().trim()
        val senha = etSenha.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Email inválido"
            etEmail.requestFocus()
            return
        }

        if (senha.length < 6) {
            etSenha.error = "Senha muito curta"
            etSenha.requestFocus()
            return
        }

        // Salva login
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("isLoggedIn", true).apply()

        // Vai para MenuActivity
        startActivity(Intent(this, MenuActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
