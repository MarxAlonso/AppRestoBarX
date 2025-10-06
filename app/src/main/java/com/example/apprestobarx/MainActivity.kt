package com.example.apprestobarx

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apprestobarx.ui.InicioActivity
import com.example.apprestobarx.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        animateLogo(binding.ivLogo)

        binding.btnLogin.setOnClickListener {
            val usuario = binding.etUsuario.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                TextUtils.isEmpty(usuario) -> {
                    binding.etUsuario.error = "Ingrese su usuario"
                    binding.etUsuario.requestFocus()
                    animateError(binding.tilUsuario)
                }
                TextUtils.isEmpty(password) -> {
                    binding.etPassword.error = "Ingrese su contraseña"
                    binding.etPassword.requestFocus()
                    animateError(binding.tilPassword)
                }
                usuario.length < 3 -> {
                    binding.etUsuario.error = "El usuario debe tener al menos 3 caracteres"
                    binding.etUsuario.requestFocus()
                    animateError(binding.tilUsuario)
                }
                password.length < 4 -> {
                    binding.etPassword.error = "La contraseña debe tener al menos 4 caracteres"
                    binding.etPassword.requestFocus()
                    animateError(binding.tilPassword)
                }
                isValidCredentials(usuario, password) -> {
                    animateSuccess(binding.btnLogin) {
                        val intent = Intent(this, InicioActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                else -> {
                    Toast.makeText(this, "Credenciales incorrectas. Intente nuevamente.", Toast.LENGTH_LONG).show()
                    animateError(binding.btnLogin)
                }
            }
        }
    }

    private fun isValidCredentials(usuario: String, password: String): Boolean {
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

        scaleX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete()
            }
        })

        scaleX.start()
        scaleY.start()
    }
}