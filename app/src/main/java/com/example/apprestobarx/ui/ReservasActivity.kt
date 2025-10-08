package com.example.apprestobarx.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.apprestobarx.MainActivity
import com.example.apprestobarx.R
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar

class ReservasActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    // Componentes del formulario
    private lateinit var rgTipoReserva: RadioGroup
    private lateinit var tilDetallesEvento: TextInputLayout
    private lateinit var etFecha: TextInputEditText
    private lateinit var etHora: TextInputEditText
    private lateinit var etNombre: TextInputEditText
    private lateinit var etPersonas: TextInputEditText
    private lateinit var btnConfirmar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservas)

        // Inicializar vistas
        drawerLayout = findViewById(R.id.drawerLayoutReservas)
        navigationView = findViewById(R.id.navigationViewReservas)

        // --- Componentes del Formulario ---
        rgTipoReserva = findViewById(R.id.rgTipoReserva)
        tilDetallesEvento = findViewById(R.id.tilDetallesEvento)
        etFecha = findViewById(R.id.etFecha)
        etHora = findViewById(R.id.etHora)
        etNombre = findViewById(R.id.etNombre)
        etPersonas = findViewById(R.id.etPersonas)
        btnConfirmar = findViewById(R.id.btnConfirmarReserva)


        // --- Configuración del Toolbar y Menú Lateral ---
        val toolbar: Toolbar = findViewById(R.id.toolbarReservas)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigationMenu()

        // --- Lógica del Formulario ---
        setupFormulario()
    }

    private fun setupNavigationMenu() {
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_platillos -> startActivity(Intent(this, InicioActivity::class.java))
                R.id.nav_bebidas -> startActivity(Intent(this, BebidasActivity::class.java))
                R.id.nav_postres -> startActivity(Intent(this, PostresActivity::class.java))
                R.id.nav_reservas -> Toast.makeText(this, "Ya estás en Reservas 📅", Toast.LENGTH_SHORT).show()
                R.id.nav_promociones -> startActivity(Intent(this, PromocionesActivity::class.java))
                R.id.nav_chatbot ->{
                    startActivity(Intent(this, ChatbotActivity::class.java))
                    finish()
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupFormulario() {
        // Lógica para mostrar/ocultar el campo de detalles del evento
        rgTipoReserva.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbEvento) {
                tilDetallesEvento.visibility = TextInputLayout.VISIBLE
            } else {
                tilDetallesEvento.visibility = TextInputLayout.GONE
            }
        }

        // Lógica para el selector de fecha
        etFecha.setOnClickListener {
            mostrarDatePicker()
        }

        // Lógica para el selector de hora
        etHora.setOnClickListener {
            mostrarTimePicker()
        }

        // Lógica del botón de confirmar
        btnConfirmar.setOnClickListener {
            if (validarCampos()) {
                // Aquí iría la lógica para enviar la reserva a tu API o base de datos
                val nombre = etNombre.text.toString()
                val personas = etPersonas.text.toString()
                Toast.makeText(this, "Reserva para $nombre ($personas personas) confirmada!", Toast.LENGTH_LONG).show()
                // Opcional: limpiar el formulario o navegar a otra pantalla
            }
        }
    }

    private fun validarCampos(): Boolean {
        if (etNombre.text.isNullOrBlank()) {
            etNombre.error = "El nombre es requerido"
            return false
        }
        if (etPersonas.text.isNullOrBlank()) {
            etPersonas.error = "El número de personas es requerido"
            return false
        }
        if (etFecha.text.isNullOrBlank()) {
            etFecha.error = "La fecha es requerida"
            Toast.makeText(this, "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
            return false
        }
        if (etHora.text.isNullOrBlank()) {
            etHora.error = "La hora es requerida"
            Toast.makeText(this, "Por favor, selecciona una hora", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun mostrarDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // Formateamos la fecha para mostrarla
            val fechaFormateada = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            etFecha.setText(fechaFormateada)
        }, year, month, day)

        // Opcional: no permitir seleccionar fechas pasadas
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun mostrarTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Formateamos la hora para mostrarla
            val horaFormateada = String.format("%02d:%02d", selectedHour, selectedMinute)
            etHora.setText(horaFormateada)
        }, hour, minute, true) // true para formato de 24 horas

        timePickerDialog.show()
    }
}