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
import com.example.apprestobarx.ui.InicioActivity
import com.example.apprestobarx.MainActivity
import com.example.apprestobarx.R
import com.example.apprestobarx.controllers.PostresAdapter
import com.example.apprestobarx.network.PostresResponse
import com.example.apprestobarx.network.RetrofitClient
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostresActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: PostresAdapter

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

        // Configurar RecyclerView
        recycler = findViewById(R.id.recyclerPostres)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = PostresAdapter(emptyList())
        recycler.adapter = adapter

        // Llamar a la API de postres
        RetrofitClient.instance.getPostres().enqueue(object : Callback<PostresResponse> {
            override fun onResponse(call: Call<PostresResponse>, response: Response<PostresResponse>) {
                if (response.isSuccessful) {
                    val lista = response.body()?.data ?: emptyList()
                    adapter.updateList(lista)
                } else {
                    Toast.makeText(this@PostresActivity, "Error al cargar postres", Toast.LENGTH_SHORT).show()
                    Log.e("API", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<PostresResponse>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}")
                Toast.makeText(this@PostresActivity, "No se pudo conectar con la API", Toast.LENGTH_SHORT).show()
            }
        })
    }
}