package com.example.apprestobarx.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.apprestobarx.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MapaActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvDistancia: TextView
    private lateinit var tvTiempo: TextView

    // Coordenadas del local (RestoBarX - Av. 2 de Octubre 1080, Los Olivos)
    private val localLocation = LatLng(-11.986618, -77.060195)
    private val localName = "RestoBarX (Torito Grill)"

    // Registro de permisos para solicitar la ubicación
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permiso concedido, obtener ubicación
                getDeviceLocation()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado. No se puede mostrar tu ubicación.", Toast.LENGTH_LONG).show()
                // Mostrar solo la ubicación del local
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(localLocation, 16f))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa)

        // Configurar la Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        tvDistancia = findViewById(R.id.tvDistancia)
        tvTiempo = findViewById(R.id.tvTiempo)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializar el mapa
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Configurar el mapa con la ubicación del local
        map.addMarker(
            MarkerOptions()
                .position(localLocation)
                .title(localName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(localLocation, 16f))

        // Revisar permisos para obtener la ubicación del usuario
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            map.isMyLocationEnabled = true
            getDeviceLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getDeviceLocation() {
        try {
            val locationResult = fusedLocationClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val lastKnownLocation: Location = task.result
                    val userLocation = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14f))

                    drawRouteAndGetInfo(userLocation, localLocation)

                } else {
                    Toast.makeText(this, "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Error al obtener ubicación: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun drawRouteAndGetInfo(origin: LatLng, destination: LatLng) {
        // Asegúrate de que R.string.google_maps_key contiene tu clave API
        val apiKey = getString(R.string.google_maps_key)
        val url = getDirectionsUrl(origin, destination, apiKey)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()

                // Leemos el cuerpo de la respuesta inmediatamente (y lo cerramos)
                val responseBodyString = response.body?.string()

                if (response.isSuccessful && !responseBodyString.isNullOrEmpty()) {

                    val directions = parseDirectionsJson(responseBodyString)

                    launch(Dispatchers.Main) {
                        if (directions.isNotEmpty()) {
                            map.addPolyline(PolylineOptions()
                                .addAll(directions)
                                .width(10f)
                                .color(Color.BLUE)
                            )
                            displayRouteInfo(responseBodyString)
                        } else {
                            // Si el parsing falló o la ruta no existe (ZERO_RESULTS)
                            Toast.makeText(this@MapaActivity, "No se encontró ruta o error de datos.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Manejo de error de OkHttp o respuesta vacía
                    launch(Dispatchers.Main) {
                        Toast.makeText(this@MapaActivity, "Error en la solicitud a Directions API. Código: ${response.code}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: IOException) {
                launch(Dispatchers.Main) {
                    Toast.makeText(this@MapaActivity, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getDirectionsUrl(origin: LatLng, dest: LatLng, apiKey: String): String {
        val strOrigin = "origin=${origin.latitude},${origin.longitude}"
        val strDest = "destination=${dest.latitude},${dest.longitude}"
        val mode = "mode=driving" // Cambia a 'walking' si es necesario
        val parameters = "$strOrigin&$strDest&$mode&key=$apiKey"
        val output = "json"
        return "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
    }

    private fun parseDirectionsJson(json: String): List<LatLng> {
        val path = mutableListOf<LatLng>()
        try {
            // CORRECCIÓN: Usar Map::class.java para evitar el error de literal de clase con genéricos
            val root = Gson().fromJson(json, Map::class.java)

            // Usamos List<*> y Map<*, *> para un casting seguro
            val routes = root["routes"] as? List<*> ?: return emptyList()

            if (routes.isNotEmpty()) {
                val route = routes[0] as? Map<*, *> ?: return emptyList()
                val legs = route["legs"] as? List<*> ?: return emptyList()
                if (legs.isNotEmpty()) {
                    val leg = legs[0] as? Map<*, *> ?: return emptyList()
                    val steps = leg["steps"] as? List<*> ?: return emptyList()

                    for (step in steps) {
                        val stepMap = step as? Map<*, *> ?: continue
                        val polyline = stepMap["polyline"] as? Map<*, *>
                        val points = polyline?.get("points") as? String

                        points?.let { path.addAll(decodePoly(it)) }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // ELIMINAMOS el bloque launch(Dispatchers.Main) para evitar el error de CoroutineScope
            return emptyList() // Devolvemos una lista vacía para indicar un fallo de parsing
        }
        return path
    }

    // Función para decodificar la cadena de puntos comprimida (algoritmo de Google)
    private fun decodePoly(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            val p = LatLng(lat.toDouble() / 1E5,
                lng.toDouble() / 1E5)
            poly.add(p)
        }

        return poly
    }

    private fun displayRouteInfo(json: String) {
        try {
            // CORRECCIÓN: Usar Map::class.java para evitar el error de literal de clase con genéricos
            val root = Gson().fromJson(json, Map::class.java)

            val routes = root["routes"] as? List<*> ?: return
            if (routes.isNotEmpty()) {
                val route = routes[0] as? Map<*, *> ?: return
                val legs = route["legs"] as? List<*> ?: return
                if (legs.isNotEmpty()) {
                    val leg = legs[0] as? Map<*, *> ?: return

                    val distanceMap = leg["distance"] as? Map<*, *>
                    val durationMap = leg["duration"] as? Map<*, *>

                    val distanceText = distanceMap?.get("text") as? String
                    val durationText = durationMap?.get("text") as? String

                    tvDistancia.text = "Distancia: $distanceText"
                    tvTiempo.text = "Tiempo de viaje (Auto): $durationText"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tvDistancia.text = "Distancia: Error de formato"
            tvTiempo.text = "Tiempo: Error de formato"
        }
    }
}