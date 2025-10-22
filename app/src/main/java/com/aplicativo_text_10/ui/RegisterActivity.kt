package com.aplicativo_text_10.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.aplicativo_text_10.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etSenha: EditText
    private lateinit var etPhone: EditText
    private lateinit var checkBoxTerms: CheckBox
    private lateinit var btnCadastrar: Button
    private lateinit var layoutBack: LinearLayout
    private lateinit var layoutSignIn: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        initViews()
        setupListeners()

        val emailFromIntent = intent.getStringExtra("EXTRA_EMAIL")
        if (!emailFromIntent.isNullOrEmpty()) {
            etEmail.setText(emailFromIntent)
        }
    }

    private fun initViews() {
        etNome = findViewById(R.id.editTextUserName)
        etEmail = findViewById(R.id.editTextEmail)
        etSenha = findViewById(R.id.editTextPassword)
        etPhone = findViewById(R.id.editTextPhone)
        checkBoxTerms = findViewById(R.id.checkBoxTerms)
        btnCadastrar = findViewById(R.id.buttonSignUp)
        layoutBack = findViewById(R.id.layoutBack)
        layoutSignIn = findViewById(R.id.layoutSignIn)
    }

    private fun setupListeners() {
        btnCadastrar.setOnClickListener { performRegister() }
        layoutBack.setOnClickListener { finish() }
        layoutSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun performRegister() {
        val nome = etNome.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val senha = etSenha.text.toString().trim()
        val phone = etPhone.text.toString().trim()

        if (nome.isEmpty()) {
            etNome.error = getString(R.string.field_required)
            etNome.requestFocus()
            return
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = getString(R.string.invalid_email)
            etEmail.requestFocus()
            return
        }

        if (senha.isEmpty() || senha.length < 6) {
            etSenha.error = getString(R.string.password_min_length)
            etSenha.requestFocus()
            return
        }

        if (phone.isEmpty()) {
            etPhone.error = getString(R.string.field_required)
            etPhone.requestFocus()
            return
        }

        if (!checkBoxTerms.isChecked) {
            Toast.makeText(this, getString(R.string.accept_terms), Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, getString(R.string.signup_success), Toast.LENGTH_SHORT).show()
        finish()
    }
}