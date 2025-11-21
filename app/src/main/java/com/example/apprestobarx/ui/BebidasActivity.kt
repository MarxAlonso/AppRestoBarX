package com.example.apprestobarx.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apprestobarx.ui.InicioActivity
import com.example.apprestobarx.MainActivity
import com.example.apprestobarx.ui.PostresActivity
import com.example.apprestobarx.R
import com.example.apprestobarx.controllers.BebidasAdapter
import com.example.apprestobarx.data.DatabaseProvider
import com.example.apprestobarx.data.repository.BebidasRepository
import com.example.apprestobarx.models.Bebidas
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class BebidasActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: BebidasAdapter
    
    private lateinit var etBuscar: EditText
    private lateinit var spPrecio: Spinner
    private lateinit var spTipo: Spinner
    
    private var originalBebidas: List<Bebidas> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bebidas)

        drawerLayout = findViewById(R.id.drawerLayoutBebidas)
        navigationView = findViewById(R.id.navigationViewBebidas)

        val toolbar: Toolbar = findViewById(R.id.toolbarBebidas)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_platillos -> {
                    startActivity(Intent(this, InicioActivity::class.java))
                    finish()
                }
                R.id.nav_bebidas -> Toast.makeText(this, "Ya estás en Bebidas 🥤", Toast.LENGTH_SHORT).show()
                R.id.nav_postres -> {
                    startActivity(Intent(this, PostresActivity::class.java))
                    finish()
                }
                R.id.nav_reservas ->{
                    startActivity(Intent(this, ReservasActivity::class.java))
                    finish()
                }
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

        recycler = findViewById(R.id.recyclerBebidas)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = BebidasAdapter(emptyList())
        recycler.adapter = adapter
        
        etBuscar = findViewById(R.id.etBuscarBebida)
        spPrecio = findViewById(R.id.spPrecioBebida)
        spTipo = findViewById(R.id.spTipoBebida)

        cargarBebidas()
    }

    private fun setupFilters() {
        // Configurar Spinner de Precios
        val precios = listOf("Todos", "Menor a 20", "20 - 50", "Mayor a 50")
        val precioAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, precios)
        precioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPrecio.adapter = precioAdapter

        // Configurar Spinner de Tipos (Dinámico)
        val tipos = mutableListOf("Todos")
        tipos.addAll(originalBebidas.map { it.type }.distinct().sorted())
        val tipoAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        tipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spTipo.adapter = tipoAdapter

        // Listeners
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterBebidas()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterBebidas()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spPrecio.onItemSelectedListener = spinnerListener
        spTipo.onItemSelectedListener = spinnerListener
    }

    private fun filterBebidas() {
        val query = etBuscar.text.toString().lowercase()
        val precioSeleccionado = spPrecio.selectedItem.toString()
        val tipoSeleccionado = spTipo.selectedItem.toString()

        val listaFiltrada = originalBebidas.filter { bebida ->
            val matchesName = bebida.name.lowercase().contains(query)
            
            val matchesPrice = when (precioSeleccionado) {
                "Menor a 20" -> bebida.price < 20
                "20 - 50" -> bebida.price in 20.0..50.0
                "Mayor a 50" -> bebida.price > 50
                else -> true
            }

            val matchesType = if (tipoSeleccionado == "Todos") true else bebida.type == tipoSeleccionado

            matchesName && matchesPrice && matchesType
        }
        
        adapter.updateList(listaFiltrada)
    }

    //Fnciona solamente llamando a la API de nodejs
    /*private fun cargarBebidas() {
        RetrofitClient.instance.getBebidas().enqueue(object : Callback<BebidasResponse> {
            override fun onResponse(call: Call<BebidasResponse>, response: Response<BebidasResponse>) {
                if (response.isSuccessful) {
                    val lista = response.body()?.data ?: emptyList()
                    adapter.updateList(lista)
                } else {
                    Toast.makeText(this@BebidasActivity, "Error al obtener bebidas", Toast.LENGTH_SHORT).show()
                    Log.e("API", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<BebidasResponse>, t: Throwable) {
                Toast.makeText(this@BebidasActivity, "Fallo en la conexión", Toast.LENGTH_SHORT).show()
                Log.e("API", "Failure: ${t.message}")
            }
        })
    }*/
    private fun cargarBebidas() {
        /*val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "restobarx.db"
        ).build()*/
        val db = DatabaseProvider.getDatabase(this)

        val repository = BebidasRepository(this, db)

        lifecycleScope.launch {
            val bebidas = repository.getBebidas()

            if (bebidas.isNotEmpty()) {
                originalBebidas = bebidas.map {
                    com.example.apprestobarx.models.Bebidas(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl,
                        type = it.type
                    )
                }
                adapter.updateList(originalBebidas)
                setupFilters()
            } else {
                Toast.makeText(this@BebidasActivity, "Sin datos disponibles", Toast.LENGTH_SHORT).show()
            }
        }
    }
}