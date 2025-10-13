package com.example.apprestobarx.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.apprestobarx.ui.InicioActivity
import com.example.apprestobarx.MainActivity
import com.example.apprestobarx.ui.PostresActivity
import com.example.apprestobarx.R
import com.example.apprestobarx.controllers.BebidasAdapter
import com.example.apprestobarx.data.AppDatabase
import com.example.apprestobarx.data.DatabaseProvider
import com.example.apprestobarx.data.repository.BebidasRepository
import com.example.apprestobarx.models.Bebidas
import com.example.apprestobarx.network.BebidasResponse
import com.example.apprestobarx.network.RetrofitClient
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BebidasActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: BebidasAdapter

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

        cargarBebidas()
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
                val lista = bebidas.map {
                    com.example.apprestobarx.models.Bebidas(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        price = it.price,
                        imageUrl = it.imageUrl,
                        type = it.type
                    )
                }
                adapter.updateList(lista)
            } else {
                Toast.makeText(this@BebidasActivity, "Sin datos disponibles", Toast.LENGTH_SHORT).show()
            }
        }
    }
}