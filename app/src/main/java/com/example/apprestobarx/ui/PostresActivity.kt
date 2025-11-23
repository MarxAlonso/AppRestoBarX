package com.example.apprestobarx.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.example.apprestobarx.MainActivity
import com.example.apprestobarx.R
import com.example.apprestobarx.controllers.PostresAdapter
import com.example.apprestobarx.data.DatabaseProvider
import com.example.apprestobarx.data.repository.PostresRepository
import com.example.apprestobarx.models.Postres
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch

class PostresActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: PostresAdapter
    
    private lateinit var etBuscar: EditText
    private lateinit var spPrecio: Spinner
    
    private var originalPostres: List<Postres> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postres)

        drawerLayout = findViewById(R.id.drawerLayoutPostres)
        navigationView = findViewById(R.id.navigationViewPostres)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarPostres)
        setSupportActionBar(toolbar)

        // Botón hamburguesa
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Acciones del menú lateral
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_platillos -> {
                    val intent = Intent(this, InicioActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_bebidas -> {
                    val intent = Intent(this, BebidasActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_postres -> Toast.makeText(this, "Ya estás en Postres 🍰", Toast.LENGTH_SHORT).show()
                R.id.nav_reservas ->{
                    startActivity(Intent(this, ReservasActivity::class.java))
                    finish()
                }
                R.id.nav_promociones -> startActivity(Intent(this, PromocionesActivity::class.java))
                R.id.nav_chatbot ->{
                    startActivity(Intent(this, ChatbotActivity::class.java))
                    finish()
                }
                R.id.nav_momentos ->{
                    startActivity(Intent(this, MomentosInolvidablesActivity::class.java))
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

        // Configurar RecyclerView
        recycler = findViewById(R.id.recyclerPostres)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = PostresAdapter(emptyList())
        recycler.adapter = adapter
        
        etBuscar = findViewById(R.id.etBuscarPostre)
        spPrecio = findViewById(R.id.spPrecioPostre)

        cargarPostres()
    }
    
    private fun setupFilters() {
        // Configurar Spinner de Precios
        val precios = listOf("Todos", "Menor a 15", "15 - 30", "Mayor a 30")
        val precioAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, precios)
        precioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spPrecio.adapter = precioAdapter

        // Listeners
        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPostres()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        spPrecio.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterPostres()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filterPostres() {
        val query = etBuscar.text.toString().lowercase()
        val precioSeleccionado = spPrecio.selectedItem.toString()

        val listaFiltrada = originalPostres.filter { postre ->
            val matchesName = postre.name.lowercase().contains(query)
            
            val matchesPrice = when (precioSeleccionado) {
                "Menor a 15" -> postre.price < 15
                "15 - 30" -> postre.price in 15.0..30.0
                "Mayor a 30" -> postre.price > 30
                else -> true
            }

            matchesName && matchesPrice
        }
        
        adapter.updateList(listaFiltrada)
    }
    private fun cargarPostres() {
        /*val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "restobarx.db"
        ).build()*/
        val db = DatabaseProvider.getDatabase(this)

        val repository = PostresRepository(this, db)

        lifecycleScope.launch {
            val postres = repository.getPostres()

            if (postres.isNotEmpty()) {
                originalPostres = postres.map {
                    com.example.apprestobarx.models.Postres(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl,
                        calories = it.calories
                    )
                }
                adapter.updateList(originalPostres)
                setupFilters()
            } else {
                Toast.makeText(this@PostresActivity, "Sin datos disponibles", Toast.LENGTH_SHORT).show()
            }
        }
    }
}