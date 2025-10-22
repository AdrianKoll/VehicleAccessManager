package com.aplicativo_text_10.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.aplicativo_text_10.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var btnCadastrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etEmail = findViewById(R.id.editTextEmail)
        etSenha = findViewById(R.id.editTextPassword)
        btnCadastrar = findViewById(R.id.buttonSignUp)

        btnCadastrar.setOnClickListener { performSignUp() }
    }

    private fun performSignUp() {
        val email = etEmail.text.toString().trim()
        val senha = etSenha.text.toString().trim()

        if (email.isEmpty() || senha.length < 6) {
            Toast.makeText(this, "Preencha corretamente os campos", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

        // Após cadastro, abre MenuActivity e limpa a pilha
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("isLoggedIn", true).apply()

        val intent = Intent(this, MenuActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)


    }
}
