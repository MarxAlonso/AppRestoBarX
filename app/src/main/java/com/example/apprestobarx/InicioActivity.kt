package com.example.apprestobarx

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apprestobarx.controllers.PlatilloAdapter
import com.example.apprestobarx.models.Platillo
import com.example.apprestobarx.network.DishesResponse
import com.example.apprestobarx.network.RetrofitClient
import com.google.android.material.textfield.TextInputEditText

class InicioActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Configura toolbar como un ActionBar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Toggle de hamburguesa
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Manejo de navegación lateral
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_platillos -> {
                    Toast.makeText(this, "Ya estás en Platillos 🍴", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_bebidas -> {
                    startActivity(Intent(this, BebidasActivity::class.java))
                    finish()
                }
                R.id.nav_postres -> {
                    startActivity(Intent(this, PostresActivity::class.java))
                    finish()
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        // Carrusel de imágenes
        val viewPager = findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPagerCarrusel)
        val listaCarrusel = listOf(
            CarruselItem(
                R.drawable.cuarto_pollo_brasa,
                "El Sabor de Casa 🍗",
                "Nuestro Pollo a la Brasa, jugoso y dorado."
            ),
            CarruselItem(
                R.drawable.plato_lomo_saltado,
                "Tradición Peruana 🇵🇪",
                "El Lomo Saltado que te transporta."
            ),
            CarruselItem(
                R.drawable.plato_ceviche,
                "Frescura del Mar 🌊",
                "Ceviche preparado al momento para ti."
            )
        )

        val carruselAdapter = CarruselAdapter(listaCarrusel)
        viewPager.adapter = carruselAdapter

        // Auto-scroll del carrusel
        handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                if (carruselAdapter.itemCount > 0) {
                    val nextItem = (viewPager.currentItem + 1) % carruselAdapter.itemCount
                    viewPager.setCurrentItem(nextItem, true)
                }
                handler.postDelayed(this, 5000)
            }
        }
        handler.postDelayed(runnable, 5000)

        // Lista de platillos con buscador
        val recycler = findViewById<RecyclerView>(R.id.recyclerPlatillos)
        val etBuscar = findViewById<TextInputEditText>(R.id.etBuscar)

        val adapter = PlatilloAdapter(emptyList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Llamada a la API
        RetrofitClient.instance.getPlatillos().enqueue(object : retrofit2.Callback<DishesResponse> {
            override fun onResponse(
                call: retrofit2.Call<DishesResponse>,
                response: retrofit2.Response<DishesResponse>
            ) {
                if (response.isSuccessful) {
                    val lista = response.body()?.data ?: emptyList()
                    adapter.updateList(lista)

                    // Activar buscador sobre la lista descargada
                    setupSearchFunctionality(etBuscar, lista, adapter)
                } else {
                    Toast.makeText(this@InicioActivity, "Error al obtener platillos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<DishesResponse>, t: Throwable) {
                Toast.makeText(this@InicioActivity, "Fallo: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupSearchFunctionality(
        searchEditText: TextInputEditText,
        originalList: List<Platillo>,
        adapter: PlatilloAdapter
    ) {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim() ?: ""
                filterPlatillos(query, originalList, adapter)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterPlatillos(query: String, originalList: List<Platillo>, adapter: PlatilloAdapter) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { platillo ->
                platillo.name.contains(query, ignoreCase = true) ||
                        platillo.price.toString().contains(query, ignoreCase = true)
            }
        }
        adapter.updateList(filteredList)

        if (filteredList.isEmpty() && query.isNotEmpty()) {
            Toast.makeText(this, "No se encontraron platillos con '$query'", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpieza del handler para evitar fugas de memoria
        handler.removeCallbacksAndMessages(null)
    }
}
