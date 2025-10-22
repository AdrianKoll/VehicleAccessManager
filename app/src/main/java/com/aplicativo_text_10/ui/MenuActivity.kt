package com.aplicativo_text_10.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.aplicativo_text_10.R

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
            return
        }

        findViewById<CardView>(R.id.cardVerificarRegistrar).setOnClickListener {
            startActivity(Intent(this, CheckVehicleActivity::class.java))
        }

        findViewById<CardView>(R.id.cardEditar).setOnClickListener {
            startActivity(Intent(this, VehicleListActivity::class.java))
        }

        findViewById<ImageView>(R.id.imageViewLogout)?.setOnClickListener {
            prefs.edit().clear().apply()  // Limpa login
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }
}
