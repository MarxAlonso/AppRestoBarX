package com.example.apprestobarx.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.apprestobarx.data.DatabaseProvider
import com.example.apprestobarx.data.local.UsuarioEntity
import com.example.apprestobarx.databinding.ActivityRegistroBinding
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DatabaseProvider.getDatabase(this)
        val usuarioDao = db.usuarioDao()

        binding.btnRegistrar.setOnClickListener {
            val usuario = binding.etNuevoUsuario.text.toString().trim()
            val pass = binding.etNuevaPassword.text.toString().trim()
            val confirm = binding.etConfirmarPassword.text.toString().trim()

            if (usuario.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != confirm) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existe = usuarioDao.obtenerUsuario(usuario)

                if (existe != null) {
                    runOnUiThread {
                        Toast.makeText(this@RegistroActivity, "Usuario ya existe", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    usuarioDao.registrar(UsuarioEntity(usuario = usuario, password = pass))
                    runOnUiThread {
                        Toast.makeText(this@RegistroActivity, "Usuario registrado ✅", Toast.LENGTH_LONG).show()
                        finish() // vuelve al login
                    }
                }
            }
        }
    }
}