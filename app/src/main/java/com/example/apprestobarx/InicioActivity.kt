package com.example.apprestobarx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

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
            when (item.itemId) {
                // Estos son los mensajes que mostrara cuando le das click a cada uno
                R.id.nav_platillos -> Toast.makeText(this, "Platillo seleccionada 🍴", Toast.LENGTH_SHORT).show()
                R.id.nav_bebidas -> {
                    val intent = Intent(this, BebidasActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_postres -> Toast.makeText(this, "Postre seleccionado 🍴", Toast.LENGTH_SHORT).show()
                R.id.nav_promos -> Toast.makeText(this, "Promociones 🔥", Toast.LENGTH_SHORT).show()
                R.id.nav_reservas -> Toast.makeText(this, "Reservas 📅", Toast.LENGTH_SHORT).show()
                R.id.nav_contacto -> Toast.makeText(this, "Contacto 📞", Toast.LENGTH_SHORT).show()
                R.id.nav_logout -> {
                    Toast.makeText(this, "Cerrando sesión...", Toast.LENGTH_SHORT).show()

                    // Esto al dar click va al login (MainActivity) para cerrar sesion
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        val recycler = findViewById<RecyclerView>(R.id.recyclerPlatillos)

        val lista = listOf(
            Platillo("Pollo a la Brasa", "S/ 25.00", R.drawable.cuarto_pollo_brasa),
            Platillo("Lomo Saltado", "S/ 20.00", R.drawable.plato_lomo_saltado),
            Platillo("Ceviche Mixto", "S/ 30.00", R.drawable.plato_ceviche)
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = PlatilloAdapter(lista)
    }
}
