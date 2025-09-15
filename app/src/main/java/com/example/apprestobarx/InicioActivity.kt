package com.example.apprestobarx

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText

class InicioActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)

        // Configura toolbar como un ActionBar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Toggle de hamburguesa para abrir y cerrar Drawer (el menu de opciones)
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Manejar clicks en el menú lateral
        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) { //Se maneja con enlaces segun en lo que planteamos en el nav_menu
                R.id.nav_platillos -> {
                    Toast.makeText(this, "Ya estás en Platillos 🍴", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_bebidas -> {
                    val intent = Intent(this, BebidasActivity::class.java)
                    startActivity(intent)
                    finish() // Para no acumular Activities
                }
                R.id.nav_postres -> {
                    val intent = Intent(this, PostresActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                /*R.id.nav_promos -> {
                    val intent = Intent(this, PromosActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_reservas -> {
                    val intent = Intent(this, ReservasActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                R.id.nav_contacto -> {
                    val intent = Intent(this, ContactoActivity::class.java)
                    startActivity(intent)
                    finish()
                }*/
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

        //Apartado de carrusel de imagenes
        val viewPager = findViewById<androidx.viewpager2.widget.ViewPager2>(R.id.viewPagerCarrusel)

        // Lista de imágenes para el carrusel
        val imagenesCarrusel = listOf(
            R.drawable.cuarto_pollo_brasa,
            R.drawable.plato_lomo_saltado,
            R.drawable.plato_ceviche
        )

        // Textos dinámicos para el carrusel
        val textosCarrusel = listOf(
            "Bienvenido a RestoBarX 🍽️",
            "Disfruta la vista de nuestros platillos 👨‍🍳",
            "Sabores únicos, tradición y frescura 🔥"
        )

        val carruselAdapter = CarruselAdapter(imagenesCarrusel, textosCarrusel)
        viewPager.adapter = carruselAdapter

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val nextItem = (viewPager.currentItem + 1) % imagenesCarrusel.size
                viewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 5000) // cada 3 segundos
            }
        }
        handler.postDelayed(runnable, 5000)


        val recycler = findViewById<RecyclerView>(R.id.recyclerPlatillos)
        val etBuscar = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.etBuscar)

        // Lista expandida de platillos
        val lista = listOf(
            Platillo("Pollo a la Brasa", "S/ 25.00", R.drawable.cuarto_pollo_brasa),
            Platillo("Lomo Saltado", "S/ 20.00", R.drawable.plato_lomo_saltado),
            Platillo("Ceviche Mixto", "S/ 30.00", R.drawable.plato_ceviche),
            Platillo("Arroz con Pollo", "S/ 18.00", R.drawable.cuarto_pollo_brasa),
            Platillo("Ají de Gallina", "S/ 22.00", R.drawable.plato_lomo_saltado),
            Platillo("Anticuchos", "S/ 15.00", R.drawable.plato_ceviche)
        )

        val adapter = PlatilloAdapter(lista.toMutableList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Búsqueda mejorada en tiempo real
         setupSearchFunctionality(etBuscar, lista, adapter)

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
                platillo.nombre.contains(query, ignoreCase = true) ||
                platillo.precio.contains(query, ignoreCase = true)
            }
        }
        adapter.updateList(filteredList)
        
        // Mostrar mensaje si no hay resultados
        if (filteredList.isEmpty() && query.isNotEmpty()) {
            Toast.makeText(this, "No se encontraron platillos con '$query'", Toast.LENGTH_SHORT).show()
        }
    }
}
