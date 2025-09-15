package com.example.apprestobarx

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Ajuste de bordes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Referencias a los elementos del XML
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val logoImageView = findViewById<ImageView>(R.id.ivLogo)

        // Animación del logo al iniciar
        animateLogo(logoImageView)

        // Acción del botón
        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validaciones mejoradas
            when {
                TextUtils.isEmpty(usuario) -> {
                    etUsuario.error = "Ingrese su usuario"
                    etUsuario.requestFocus()
                    animateError(etUsuario)
                }
                TextUtils.isEmpty(password) -> {
                    etPassword.error = "Ingrese su contraseña"
                    etPassword.requestFocus()
                    animateError(etPassword)
                }
                usuario.length < 3 -> {
                    etUsuario.error = "El usuario debe tener al menos 3 caracteres"
                    etUsuario.requestFocus()
                    animateError(etUsuario)
                }
                password.length < 4 -> {
                    etPassword.error = "La contraseña debe tener al menos 4 caracteres"
                    etPassword.requestFocus()
                    animateError(etPassword)
                }
                isValidCredentials(usuario, password) -> {
                    // Login correcto
                    animateSuccess(btnLogin) {
                        val intent = Intent(this, InicioActivity::class.java)
                        startActivity(intent)
                        finish() // cierra la pantalla de login
                    }
                }
                else -> {
                    Toast.makeText(this, "Credenciales incorrectas. Intente nuevamente.", Toast.LENGTH_LONG).show()
                    animateError(btnLogin)
                }
            }
        }
    }

    private fun isValidCredentials(usuario: String, password: String): Boolean {
        // Múltiples usuarios válidos para mayor flexibilidad
        val validCredentials = mapOf(
            "admin" to "1234",
            "restobarx" to "admin",
            "usuario" to "password"
        )
        return validCredentials[usuario.lowercase()] == password
    }

    private fun animateLogo(view: View) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1.0f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0.0f, 1.0f)
        
        scaleX.duration = 1000
        scaleY.duration = 1000
        alpha.duration = 1000
        
        scaleX.interpolator = AccelerateDecelerateInterpolator()
        scaleY.interpolator = AccelerateDecelerateInterpolator()
        alpha.interpolator = AccelerateDecelerateInterpolator()
        
        scaleX.start()
        scaleY.start()
        alpha.start()
    }

    private fun animateError(view: View) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)
        shake.duration = 600
        shake.start()
    }

    private fun animateSuccess(view: View, onComplete: () -> Unit) {
        val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, 1.1f, 1.0f)
        val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, 1.1f, 1.0f)
        
        scaleX.duration = 300
        scaleY.duration = 300
        
        scaleX.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onComplete()
            }
        })
        
        scaleX.start()
        scaleY.start()
    }
}
