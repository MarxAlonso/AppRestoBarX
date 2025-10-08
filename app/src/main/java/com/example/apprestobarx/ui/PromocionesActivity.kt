package com.example.apprestobarx.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apprestobarx.MainActivity
import com.example.apprestobarx.R
import com.example.apprestobarx.controllers.PromocionesAdapter
import com.example.apprestobarx.network.PromocionesResponse
import com.example.apprestobarx.network.RetrofitClient
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PromocionesActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: PromocionesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promociones)

        drawerLayout = findViewById(R.id.drawerLayoutPromociones)
        navigationView = findViewById(R.id.navigationViewPromociones)
        val toolbar: Toolbar = findViewById(R.id.toolbarPromociones)
        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_platillos -> startActivity(Intent(this, InicioActivity::class.java))
                R.id.nav_bebidas -> startActivity(Intent(this, BebidasActivity::class.java))
                R.id.nav_postres -> startActivity(Intent(this, PostresActivity::class.java))
                R.id.nav_promociones -> Toast.makeText(this, "Ya estás en Promociones 🎉", Toast.LENGTH_SHORT).show()
                R.id.nav_chatbot -> startActivity(Intent(this, ChatbotActivity::class.java))
                R.id.nav_logout -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawers()
            true
        }

        recycler = findViewById(R.id.recyclerPromociones)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = PromocionesAdapter(emptyList())
        recycler.adapter = adapter

        cargarPromociones()
    }

    private fun cargarPromociones() {
        RetrofitClient.instance.getPromociones().enqueue(object : Callback<PromocionesResponse> {
            override fun onResponse(call: Call<PromocionesResponse>, response: Response<PromocionesResponse>) {
                if (response.isSuccessful) {
                    val lista = response.body()?.data ?: emptyList()
                    adapter.updateList(lista)
                } else {
                    Toast.makeText(this@PromocionesActivity, "Error al obtener promociones", Toast.LENGTH_SHORT).show()
                    Log.e("API", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PromocionesResponse>, t: Throwable) {
                Toast.makeText(this@PromocionesActivity, "Fallo en la conexión", Toast.LENGTH_SHORT).show()
                Log.e("API", "Failure: ${t.message}")
            }
        })
    }
}
