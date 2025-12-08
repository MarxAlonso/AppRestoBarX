package com.example.apprestobarx.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apprestobarx.MainActivity
import com.example.apprestobarx.R
import com.example.apprestobarx.controllers.ChatAdapter
import com.example.apprestobarx.data.DatabaseProvider
import com.example.apprestobarx.data.local.NotificacionReservaEntity
import com.example.apprestobarx.models.Message
import com.example.apprestobarx.models.Reservation
import com.example.apprestobarx.network.DishesResponse
import com.example.apprestobarx.network.BebidasResponse
import com.example.apprestobarx.network.PostresResponse
import com.example.apprestobarx.network.ReservationsListResponse
import com.example.apprestobarx.network.RetrofitClient
import com.example.apprestobarx.utils.NotificacionReceiver
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatbotActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recyclerChat: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: Button
    private lateinit var chatAdapter: ChatAdapter // Adapter para el RecyclerView

    private var esperandoNombreReserva = false
    private var esperandoSeleccionReserva = false
    private var listaReservasUsuario: List<Reservation>? = null

    private var reservaSeleccionada: Reservation? = null
    private var esperandoConfirmacionNotificacion = false

    private val COSTO_RESERVA_MESA = 5.00 // 5.00 por persona
    private val COSTO_ALQUILER_LOCAL = 500.00 // 500.00 por evento/noche

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        // Inicializar vistas
        drawerLayout = findViewById(R.id.drawerLayoutChatbot)
        navigationView = findViewById(R.id.navigationViewChatbot)
        recyclerChat = findViewById(R.id.recyclerChat)
        etMessage = findViewById(R.id.etMessage)
        btnSend = findViewById(R.id.btnSend)

        // Configuración de la Toolbar y Navigation Drawer
        val toolbar: Toolbar = findViewById(R.id.toolbarChatbot)
        setSupportActionBar(toolbar)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setupDrawerNavigation()

        // Configuración del RecyclerView del Chat
        chatAdapter = ChatAdapter { option -> // Lambda para manejar la selección de opciones
            etMessage.setText(option)
            sendMessage()
        }
        recyclerChat.layoutManager = LinearLayoutManager(this)
        recyclerChat.adapter = chatAdapter

        // Mensaje de bienvenida y opciones iniciales
        addBotMessage("¡Hola! Soy el Chatbot de Resto BarX. ¿En qué puedo ayudarte?",
            listOf("Costo de Reservas", "Alquiler del Local", "Platillo más Caro", "Menú"))

        // Listener del botón de enviar
        btnSend.setOnClickListener {
            sendMessage()
        }
        solicitarPermisos()
    }

    private fun setupDrawerNavigation() {
        navigationView.setNavigationItemSelectedListener { item ->
            // Implementación de la navegación similar a BebidasActivity
            when (item.itemId) {
                R.id.nav_platillos -> startActivity(Intent(this, InicioActivity::class.java))
                R.id.nav_bebidas -> startActivity(Intent(this, BebidasActivity::class.java))
                R.id.nav_postres -> startActivity(Intent(this, PostresActivity::class.java))
                R.id.nav_reservas -> startActivity(Intent(this, ReservasActivity::class.java))
                R.id.nav_chatbot -> Toast.makeText(this, "Ya estás en Chat Bot 📅", Toast.LENGTH_SHORT).show()
                R.id.nav_promociones -> startActivity(Intent(this, PromocionesActivity::class.java))
                R.id.nav_ubicacion ->{
                    startActivity(Intent(this, MapaActivity::class.java))
                    finish()
                }
                R.id.nav_logout -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun sendMessage() {
        val userMessage = etMessage.text.toString().trim()
        if (userMessage.isNotEmpty()) {
            addUserMessage(userMessage)
            etMessage.setText("")
            processUserMessage(userMessage)
        }
    }

    private fun addUserMessage(text: String) {
        chatAdapter.addMessage(Message(text, isUser = true))
        recyclerChat.scrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun addBotMessage(text: String, options: List<String>? = null) {
        chatAdapter.addMessage(Message(text, isUser = false, options = options))
        recyclerChat.scrollToPosition(chatAdapter.itemCount - 1)
    }

    // --- Lógica Central del Chatbot ---
    private fun processUserMessage(message: String) {
        val lowerCaseMessage = message.lowercase().trim()

        // Si el chatbot está esperando el nombre para buscar reserva
        if (esperandoNombreReserva) {
            val nombre = message.trim()
            if (nombre.isBlank()) {
                addBotMessage("Por favor, ingresa un nombre válido sin espacios vacíos al inicio o al final.")
                return
            }
            buscarReservaPorNombre(nombre)
            esperandoNombreReserva = false
            return
        }

        if (esperandoSeleccionReserva) {
            val numero = message.toIntOrNull()
            val lista = listaReservasUsuario
            if (lista != null && numero != null && numero in 1..lista.size) {
                val reservaElegida = lista[numero - 1]
                mostrarDetallesReserva(reservaElegida)
            } else {
                addBotMessage("Por favor, elige un número válido de la lista anterior.")
            }
            esperandoSeleccionReserva = false
            return
        }
        if (lowerCaseMessage.contains("whatsapp")) {
            reservaSeleccionada?.let { enviarWhatsApp(it) }
            return
        }

        // Confirmación de notificación
        if (esperandoConfirmacionNotificacion) {
            val respuesta = message.lowercase()
            if (respuesta.contains("sí")) {
                reservaSeleccionada?.let { programarNotificacion(it) }
                addBotMessage("🔔 ¡Perfecto! Te avisaremos una hora antes de tu reserva.")
            } else {
                addBotMessage("De acuerdo 😊, no se ha configurado ninguna notificación.")
            }
            esperandoConfirmacionNotificacion = false
            return
        }

        when {
            lowerCaseMessage.contains("hola") || lowerCaseMessage.contains("saludos") -> {
                addBotMessage(
                    "¡Hola! ¿Sobre qué deseas saber?",
                    listOf("Platillos", "Bebidas", "Postres", "Costo de Reservas", "Alquiler del Local", "Consultar Reservas")
                )
            }

            lowerCaseMessage.contains("reserva") && lowerCaseMessage.contains("costo") -> {
                addBotMessage("El costo de **reservar una mesa** es de **\$$COSTO_RESERVA_MESA** por persona.")
            }

            lowerCaseMessage.contains("consultar") && lowerCaseMessage.contains("reserva") -> {
                addBotMessage("Perfecto 😊, por favor ingresa el **nombre de la persona** con la que se hizo la reserva.")
                esperandoNombreReserva = true
            }

            lowerCaseMessage.contains("alquiler") || lowerCaseMessage.contains("evento") -> {
                addBotMessage("El **alquiler del local** cuesta **\$$COSTO_ALQUILER_LOCAL** por noche.")
            }

            // Otras funciones ya existentes
            lowerCaseMessage.contains("platillo") && lowerCaseMessage.contains("caro") -> cargarPlatilloExtremo(true)
            lowerCaseMessage.contains("platillo") && lowerCaseMessage.contains("barato") -> cargarPlatilloExtremo(false)
            lowerCaseMessage.contains("bebida") && lowerCaseMessage.contains("cara") -> cargarBebidaExtrema(true)
            lowerCaseMessage.contains("bebida") && lowerCaseMessage.contains("barata") -> cargarBebidaExtrema(false)
            lowerCaseMessage.contains("postre") && lowerCaseMessage.contains("caro") -> cargarPostreExtremo(true)
            lowerCaseMessage.contains("postre") && lowerCaseMessage.contains("barato") -> cargarPostreExtremo(false)
            lowerCaseMessage.contains("platillo") -> listarPlatillos()
            lowerCaseMessage.contains("bebida") -> listarBebidas()
            lowerCaseMessage.contains("postre") -> listarPostres()

            else -> {
                addBotMessage(
                    "No entendí bien. ¿Quieres consultar algo del menú o tus reservas?",
                    listOf("Platillos", "Bebidas", "Postres", "Consultar Reservas")
                )
            }
        }
    }


    // --- Funciones para la API ---

    private fun cargarPlatilloExtremo(isMax: Boolean) {
        RetrofitClient.instance.getPlatillos().enqueue(object : Callback<DishesResponse> {
            override fun onResponse(call: Call<DishesResponse>, response: Response<DishesResponse>) {
                if (response.isSuccessful) {
                    val platos = response.body()?.data ?: emptyList()
                    if (platos.isNotEmpty()) {
                        val platillo = if (isMax) {
                            platos.maxByOrNull { it.price }
                        } else {
                            platos.minByOrNull { it.price }
                        }
                        platillo?.let {
                            val tipo = if (isMax) "más caro" else "más barato"
                            addBotMessage("El **platillo $tipo** es **${it.name}** con un precio de **$${it.price}**.")
                        }
                    } else {
                        addBotMessage("No hay platillos disponibles en este momento.")
                    }
                } else {
                    addBotMessage("Ocurrió un error al consultar el menú.")
                    Log.e("ChatbotAPI", "Error Platillos: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<DishesResponse>, t: Throwable) {
                addBotMessage("Fallo la conexión con el servidor. Intenta de nuevo más tarde.")
                Log.e("ChatbotAPI", "Failure Platillos: ${t.message}")
            }
        })
    }

    private fun cargarBebidaExtrema(isMax: Boolean) {
        RetrofitClient.instance.getBebidas().enqueue(object : Callback<BebidasResponse> {
            override fun onResponse(call: Call<BebidasResponse>, response: Response<BebidasResponse>) {
                if (response.isSuccessful) {
                    val bebidas = response.body()?.data ?: emptyList()
                    if (bebidas.isNotEmpty()) {
                        val bebida = if (isMax) {
                            bebidas.maxByOrNull { it.price }
                        } else {
                            bebidas.minByOrNull { it.price }
                        }
                        bebida?.let {
                            val tipo = if (isMax) "más cara" else "más barata"
                            addBotMessage("La **bebida $tipo** es **${it.name}** con un precio de **$${it.price}**.")
                        }
                    } else {
                        addBotMessage("No hay bebidas disponibles en este momento.")
                    }
                } else {
                    addBotMessage("Ocurrió un error al consultar las bebidas.")
                }
            }

            override fun onFailure(call: Call<BebidasResponse>, t: Throwable) {
                addBotMessage("Fallo la conexión con el servidor. Intenta de nuevo más tarde.")
            }
        })
    }

    private fun cargarPostreExtremo(isMax: Boolean) {
        RetrofitClient.instance.getPostres().enqueue(object : Callback<PostresResponse> {
            override fun onResponse(call: Call<PostresResponse>, response: Response<PostresResponse>) {
                if (response.isSuccessful) {
                    val postres = response.body()?.data ?: emptyList()
                    if (postres.isNotEmpty()) {
                        val postre = if (isMax) {
                            postres.maxByOrNull { it.price }
                        } else {
                            postres.minByOrNull { it.price }
                        }
                        postre?.let {
                            val tipo = if (isMax) "más caro" else "más barato"
                            addBotMessage("El **postre $tipo** es **${it.name}** con un precio de **$${it.price}**.")
                        }
                    } else {
                        addBotMessage("No hay postres disponibles en este momento.")
                    }
                } else {
                    addBotMessage("Ocurrió un error al consultar los postres.")
                }
            }

            override fun onFailure(call: Call<PostresResponse>, t: Throwable) {
                addBotMessage("Fallo la conexión con el servidor. Intenta de nuevo más tarde.")
            }
        })
    }

    private fun listarPlatillos() {
        RetrofitClient.instance.getPlatillos().enqueue(object : Callback<DishesResponse> {
            override fun onResponse(call: Call<DishesResponse>, response: Response<DishesResponse>) {
                if (response.isSuccessful) {
                    val platillos = response.body()?.data ?: emptyList()
                    if (platillos.isNotEmpty()) {
                        val lista = platillos.joinToString("\n") { "- ${it.name}: $${it.price}" }
                        addBotMessage("Estos son nuestros platillos:\n$lista")
                    } else {
                        addBotMessage("No hay platillos disponibles.")
                    }
                } else {
                    addBotMessage("Error al cargar los platillos.")
                }
            }

            override fun onFailure(call: Call<DishesResponse>, t: Throwable) {
                addBotMessage("Error de conexión al cargar los platillos.")
            }
        })
    }

    private fun listarBebidas() {
        RetrofitClient.instance.getBebidas().enqueue(object : Callback<BebidasResponse> {
            override fun onResponse(call: Call<BebidasResponse>, response: Response<BebidasResponse>) {
                if (response.isSuccessful) {
                    val bebidas = response.body()?.data ?: emptyList()
                    if (bebidas.isNotEmpty()) {
                        val lista = bebidas.joinToString("\n") { "- ${it.name} (${it.type}): $${it.price}" }
                        addBotMessage("Estas son nuestras bebidas:\n$lista")
                    } else {
                        addBotMessage("No hay bebidas disponibles.")
                    }
                } else {
                    addBotMessage("Error al cargar las bebidas.")
                }
            }

            override fun onFailure(call: Call<BebidasResponse>, t: Throwable) {
                addBotMessage("Error de conexión al cargar las bebidas.")
            }
        })
    }

    private fun listarPostres() {
        RetrofitClient.instance.getPostres().enqueue(object : Callback<PostresResponse> {
            override fun onResponse(call: Call<PostresResponse>, response: Response<PostresResponse>) {
                if (response.isSuccessful) {
                    val postres = response.body()?.data ?: emptyList()
                    if (postres.isNotEmpty()) {
                        val lista = postres.joinToString("\n") { "- ${it.name}: $${it.price} (${it.calories} cal)" }
                        addBotMessage("Estos son nuestros postres:\n$lista")
                    } else {
                        addBotMessage("No hay postres disponibles.")
                    }
                } else {
                    addBotMessage("Error al cargar los postres.")
                }
            }

            override fun onFailure(call: Call<PostresResponse>, t: Throwable) {
                addBotMessage("Error de conexión al cargar los postres.")
            }
        })
    }

    private fun buscarReservaPorNombre(nombre: String) {
        RetrofitClient.instance.getReservations().enqueue(object : Callback<ReservationsListResponse> {
            override fun onResponse(call: Call<ReservationsListResponse>, response: Response<ReservationsListResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val reservas = response.body()!!.data.filter { it.fullName.equals(nombre, ignoreCase = true) }

                    if (reservas.isNotEmpty()) {
                        listaReservasUsuario = reservas
                        if (reservas.size == 1) {
                            mostrarDetallesReserva(reservas.first())
                        } else {
                            val lista = reservas.mapIndexed { index, r ->
                                "${index + 1}. ${r.reservationType} - ${r.reservationDate} a las ${r.reservationTime}"
                            }.joinToString("\n")

                            addBotMessage(
                                "Tienes ${reservas.size} reservas registradas, elige una escribiendo el número correspondiente:\n\n$lista"
                            )
                            esperandoSeleccionReserva = true
                        }
                    } else {
                        addBotMessage("❌ No se encontró ninguna reserva a nombre de **$nombre**.")
                    }
                } else {
                    addBotMessage("⚠️ Ocurrió un error al consultar las reservas.")
                }
            }

            override fun onFailure(call: Call<ReservationsListResponse>, t: Throwable) {
                addBotMessage("❌ Error de conexión con el servidor: ${t.message}")
            }
        })
    }

    private fun mostrarDetallesReserva(reserva: Reservation) {
        val detalles = """
        ✅ *Reserva encontrada*
        👤 Nombre: ${reserva.fullName}
        🪑 Tipo: ${reserva.reservationType}
        👥 Personas: ${reserva.numPeople}
        📅 Fecha: ${reserva.reservationDate}
        🕓 Hora: ${reserva.reservationTime}
        📝 Detalles: ${reserva.eventDetails ?: "Ninguno"}
    """.trimIndent()

        addBotMessage(
            "$detalles\n\n¿Deseas recibir una notificación una hora antes o comunicarte por WhatsApp?",
            listOf("Sí, notificarme", "No, gracias", "Contactar por WhatsApp")
        )

        reservaSeleccionada = reserva
        esperandoConfirmacionNotificacion = true
    }


    private fun postreMasCalorias() {
        RetrofitClient.instance.getPostres().enqueue(object : Callback<PostresResponse> {
            override fun onResponse(call: Call<PostresResponse>, response: Response<PostresResponse>) {
                if (response.isSuccessful) {
                    val postres = response.body()?.data ?: emptyList()
                    val postreMax = postres.maxByOrNull { it.calories }
                    postreMax?.let {
                        addBotMessage("El postre con **más calorías** es **${it.name}** con **${it.calories} cal** y un precio de **$${it.price}**.")
                    } ?: addBotMessage("No hay información de calorías.")
                } else {
                    addBotMessage("Error al consultar los postres.")
                }
            }

            override fun onFailure(call: Call<PostresResponse>, t: Throwable) {
                addBotMessage("Error de conexión al consultar los postres.")
            }
        })
    }

    private fun programarNotificacion(reserva: Reservation) {
        val db = DatabaseProvider.getDatabase(this)
        val dao = db.notificacionReservaDao()

        lifecycleScope.launch {
            val entity = NotificacionReservaEntity(
                nombreCliente = reserva.fullName,
                fecha = reserva.reservationDate,
                hora = reserva.reservationTime,
                mensaje = "Tienes una reserva hoy a las ${reserva.reservationTime}. Consulta al chatbot."
            )
            dao.insert(entity)

            try {
                val formato = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm")
                val fechaHora = formato.parse("${reserva.reservationDate} ${reserva.reservationTime}")

                val calendar = java.util.Calendar.getInstance().apply {
                    time = fechaHora!!
                    add(java.util.Calendar.HOUR_OF_DAY, -1) // resta 1 hora
                }

                val intent = Intent(this@ChatbotActivity, NotificacionReceiver::class.java).apply {
                    putExtra("mensaje", entity.mensaje)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    this@ChatbotActivity,
                    entity.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Toast.makeText(this@ChatbotActivity, "⚠️ Habilita el permiso para alarmas exactas en ajustes.", Toast.LENGTH_LONG).show()
                        val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        intent.data = android.net.Uri.parse("package:$packageName")
                        startActivity(intent)
                        return@launch
                    }
                }

                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }catch (e: SecurityException) {
                Log.e("ChatbotActivity", "No se tiene permiso para programar alarmas exactas: ${e.message}")
                Toast.makeText(this@ChatbotActivity, "No se tiene permiso para programar alarmas exactas", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Log.e("ChatbotActivity", "Error programando notificación: ${e.message}")
            }
        }
    }
    private fun solicitarPermisos() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = android.net.Uri.parse("package:$packageName")
                startActivity(intent)
            }
        }
    }

    private fun enviarWhatsApp(reserva: Reservation) {

        val numeroEmpresa = "51922061911"  // +51 Perú (sin +)
        val mensaje = """
        Hola, deseo confirmar mi reserva:
        👤 Nombre: ${reserva.fullName}
        🪑 Tipo: ${reserva.reservationType}
        👥 Personas: ${reserva.numPeople}
        📅 Fecha: ${reserva.reservationDate}
        🕓 Hora: ${reserva.reservationTime}
        
        Pago será en efectivo o billetera digital el día del evento.
    """.trimIndent()

        val url = "https://wa.me/$numeroEmpresa?text=${Uri.encode(mensaje)}"

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No se pudo abrir WhatsApp", Toast.LENGTH_SHORT).show()
        }
    }


}