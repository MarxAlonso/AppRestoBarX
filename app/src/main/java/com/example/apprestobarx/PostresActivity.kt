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

class PostresActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postres)

        drawerLayout = findViewById(R.id.drawerLayoutPostres)
        navigationView = findViewById(R.id.navigationViewPostres)

        // Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbarPostres)
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

        // Lista de postres
        val recycler = findViewById<RecyclerView>(R.id.recyclerPostres)
        val listaPostres = listOf(
            Platillo("Suspiro a la Limeña", "S/ 12.00", R.drawable.postre_suspiro_limeno),
            Platillo("Mazamorra Morada", "S/ 8.00", R.drawable.postre_mazamorra_morada),
            Platillo("Helado Artesanal", "S/ 10.00", R.drawable.postre_helado_artesanal)
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = PlatilloAdapter(listaPostres)
    }
}