package com.example.apprestobarx.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.apprestobarx.data.AppDatabase
import com.example.apprestobarx.data.DatabaseProvider
import com.example.apprestobarx.data.local.MomentoEntity
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import com.example.apprestobarx.R

class MomentosInolvidablesActivity : AppCompatActivity() {

    private lateinit var imgPreview: ImageView
    private lateinit var etDescripcion: EditText
    private lateinit var db: AppDatabase
    private var currentImagePath: String? = null
    private lateinit var canvasContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_momentos_inolvidables)

        val toolbar: Toolbar = findViewById(R.id.toolbarMomentos)
        setSupportActionBar(toolbar)

        imgPreview = findViewById(R.id.imgPreview)
        etDescripcion = findViewById(R.id.etDescripcion)

        db = DatabaseProvider.getDatabase(this)

        // Seleccionar imagen
        findViewById<Button>(R.id.btnSeleccionarFoto).setOnClickListener {
            pickImageFromGallery()
        }

        // Tomar foto
        findViewById<Button>(R.id.btnTomarFoto).setOnClickListener {
            takePhoto()
        }

        findViewById<Button>(R.id.btnGuardar).setOnClickListener {
            val finalBitmap = exportarImagenFinal()
            currentImagePath = saveBitmap(finalBitmap)
            guardarMomento()
        }

        findViewById<Button>(R.id.btnCompartir).setOnClickListener {
            val finalBitmap = exportarImagenFinal()
            currentImagePath = saveBitmap(finalBitmap)
            shareImage(currentImagePath!!)
        }

        canvasContainer = findViewById(R.id.canvasContainer)

        findViewById<Button>(R.id.btnAgregarSticker).setOnClickListener {
            agregarSticker(R.drawable.logo_restobarx)
        }

    }

    private fun guardarMomento() {
        val desc = etDescripcion.text.toString()
        val path = currentImagePath ?: return

        lifecycleScope.launch {
            db.momentosDao().insert(
                MomentoEntity(
                    imagePath = path,
                    descripcion = desc,
                    fecha = System.currentTimeMillis()
                )
            )
            Toast.makeText(this@MomentosInolvidablesActivity, "Guardado ⭐", Toast.LENGTH_SHORT).show()
        }
    }

    // --- Seleccionar imagen ---
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uri = result.data?.data
        uri?.let {
            imgPreview.setImageURI(it)
            currentImagePath = saveImageLocally(it)
        }
    }

    // --- Tomar foto ---
    private fun takePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val bitmap = result.data?.extras?.get("data") as? Bitmap ?: return@registerForActivityResult
            imgPreview.setImageBitmap(bitmap)
            currentImagePath = saveBitmap(bitmap)
        }

    // --- Guardar internamente ---
    private fun saveImageLocally(uri: Uri): String {
        val input = contentResolver.openInputStream(uri)
        val file = File(filesDir, "momento_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { input?.copyTo(it) }
        return file.absolutePath
    }

    private fun saveBitmap(bitmap: Bitmap): String {
        val file = File(filesDir, "momento_${System.currentTimeMillis()}.jpg")
        val out = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        out.close()
        return file.absolutePath
    }

    // --- Compartir ---
    private fun shareImage(path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(Intent.createChooser(intent, "Compartir con..."))
    }

    private fun shareToWhatsapp(numero: String, path: String) {
        val file = File(path)
        val uri = FileProvider.getUriForFile(this, "$packageName.provider", file)

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.`package` = "com.whatsapp"
        intent.putExtra("jid", "$numero@s.whatsapp.net")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        startActivity(intent)
    }

    private fun agregarSticker(res: Int) {
        val sticker = ImageView(this)
        sticker.setImageResource(res)
        // Tamaño ajustado
        val params = FrameLayout.LayoutParams(200, 200)
        // Centrar el sticker inicialmente
        params.gravity = android.view.Gravity.CENTER
        sticker.layoutParams = params

        sticker.setOnTouchListener(object : View.OnTouchListener {
            var dX = 0f
            var dY = 0f
            var startX = 0f
            var startY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dX = v.x - event.rawX
                        dY = v.y - event.rawY
                        startX = event.rawX
                        startY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        v.animate()
                            .x(event.rawX + dX)
                            .y(event.rawY + dY)
                            .setDuration(0)
                            .start()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        // Detección de click simple (opcional)
                        if (Math.abs(event.rawX - startX) < 10 && Math.abs(event.rawY - startY) < 10) {
                            v.performClick()
                        }
                        return true
                    }
                }
                return false
            }
        })

        canvasContainer.addView(sticker)
    }

    private fun exportarImagenFinal(): Bitmap {
        val bitmap = Bitmap.createBitmap(canvasContainer.width, canvasContainer.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvasContainer.draw(canvas)
        return bitmap
    }
}
