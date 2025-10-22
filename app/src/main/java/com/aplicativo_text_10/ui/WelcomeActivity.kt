package com.aplicativo_text_10.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.aplicativo_text_10.R

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            // Já logado, vai direto para MenuActivity
            startActivity(Intent(this, MenuActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_welcome)

        val btnSignIn: Button = findViewById(R.id.buttonSignIn)
        val btnSignUp: Button = findViewById(R.id.buttonSignUp)

        val btnFacebook: ImageButton = findViewById(R.id.buttonFacebook)
        val btnTwitter: ImageButton = findViewById(R.id.buttonTwitter)
        val btnLinkedIn: ImageButton = findViewById(R.id.buttonLinkedIn)

        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnFacebook.setOnClickListener {
            // Placeholder
        }

        btnTwitter.setOnClickListener {
            // Placeholder
        }

        btnLinkedIn.setOnClickListener {
            // Placeholder
        }
    }
}
