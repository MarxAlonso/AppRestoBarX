package com.example.apprestobarx

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class BebidasActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bebidas)

        drawerLayout = findViewById(R.id.drawerLayoutBebidas)
        navigationView = findViewById(R.id.navigationViewBebidas)

        // Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbarBebidas)
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
                R.id.nav_bebidas -> Toast.makeText(this, "Ya estás en Bebidas 🥤", Toast.LENGTH_SHORT).show()
                R.id.nav_postres -> {
                    val intent = Intent(this, PostresActivity::class.java)
                    startActivity(intent)
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

        // Lista de bebidas
        val recycler = findViewById<RecyclerView>(R.id.recyclerBebidas)
        val listaBebidas = listOf(
            Platillo("Chicha Morada", "S/ 18.00", R.drawable.jarra_chicha_morada),
            Platillo("Inca Kola 1L", "S/ 10.00", R.drawable.bebida_inca_kola),
            Platillo("Cerveza Cusqueña", "S/ 12.00", R.drawable.bebida_cusquena)
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = PlatilloAdapter(listaBebidas)
    }
}
