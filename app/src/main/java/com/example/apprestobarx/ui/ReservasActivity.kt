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
import androidx.drawerlayout.widget.DrawerLayout
import com.example.apprestobarx.MainActivity
import com.example.apprestobarx.R
import com.example.apprestobarx.models.Reservation
import com.example.apprestobarx.network.ReservationResponse
import com.example.apprestobarx.network.RetrofitClient
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReservasActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

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

        drawerLayout = findViewById(R.id.drawerLayoutReservas)
        navigationView = findViewById(R.id.navigationViewReservas)
        rgTipoReserva = findViewById(R.id.rgTipoReserva)
        tilDetallesEvento = findViewById(R.id.tilDetallesEvento)
        etFecha = findViewById(R.id.etFecha)
        etHora = findViewById(R.id.etHora)
        etNombre = findViewById(R.id.etNombre)
        etPersonas = findViewById(R.id.etPersonas)
        btnConfirmar = findViewById(R.id.btnConfirmarReserva)

        val toolbar: Toolbar = findViewById(R.id.toolbarReservas)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setupNavigationMenu()
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
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, ChatbotActivity::class.java))
                    finish()
                }
                R.id.nav_ubicacion ->{
                    startActivity(Intent(this, MapaActivity::class.java))
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
        rgTipoReserva.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbEvento) {
                tilDetallesEvento.visibility = TextInputLayout.VISIBLE
            } else {
                tilDetallesEvento.visibility = TextInputLayout.GONE
            }
        }

        etFecha.setOnClickListener { mostrarDatePicker() }
        etHora.setOnClickListener { mostrarTimePicker() }

        btnConfirmar.setOnClickListener {
            if (validarCampos()) {
                val tipoReserva = if (rgTipoReserva.checkedRadioButtonId == R.id.rbEvento) "Evento" else "Mesa"
                val nombre = etNombre.text.toString().trim()
                val personas = etPersonas.text.toString().toInt()
                val fecha = etFecha.text.toString().trim()
                val hora = etHora.text.toString().trim()
                val detalles = if (tipoReserva == "Evento") findViewById<TextInputEditText>(R.id.etDetallesEvento).text.toString().trim() else null

                val reserva = Reservation(
                    reservationType = tipoReserva,
                    fullName = nombre,
                    numPeople = personas,
                    reservationDate = fecha,
                    reservationTime = hora,
                    eventDetails = detalles
                )

                enviarReserva(reserva)
            }
        }
    }

    private fun enviarReserva(reserva: Reservation) {
        val call = RetrofitClient.instance.createReservation(reserva)

        call.enqueue(object : Callback<ReservationResponse> {
            override fun onResponse(
                call: Call<ReservationResponse>,
                response: Response<ReservationResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val res = response.body()!!
                    if (res.success) {
                        Toast.makeText(
                            this@ReservasActivity,
                            "✅ ${res.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        limpiarCampos()
                    } else {
                        Toast.makeText(
                            this@ReservasActivity,
                            "⚠️ ${res.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ReservasActivity,
                        "Error: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<ReservationResponse>, t: Throwable) {
                Toast.makeText(
                    this@ReservasActivity,
                    "❌ Error al conectar con el servidor: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun limpiarCampos() {
        etNombre.text?.clear()
        etPersonas.text?.clear()
        etFecha.text?.clear()
        etHora.text?.clear()
        findViewById<TextInputEditText>(R.id.etDetallesEvento).text?.clear()
        rgTipoReserva.check(R.id.rbMesa)
        tilDetallesEvento.visibility = TextInputLayout.GONE
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
            val fechaFormateada = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
            etFecha.setText(fechaFormateada)
        }, year, month, day)

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun mostrarTimePicker() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val horaFormateada = String.format("%02d:%02d", selectedHour, selectedMinute)
            etHora.setText(horaFormateada)
        }, hour, minute, true)

        timePickerDialog.show()
    }
}
