package com.example.a3bi_bruno_ver1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class ImageUploadActivity : AppCompatActivity() {
    private lateinit var selectFileButton: Button
    private lateinit var uploadFileButton: Button
    private lateinit var galleryActivity: Button
    private lateinit var logoutButton: Button
    private lateinit var imageView: ImageView
    private var fileUri: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val PICK_FILE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_upload)

        storage = Firebase.storage
        auth = Firebase.auth

        selectFileButton = findViewById(R.id.select_file_button)
        uploadFileButton = findViewById(R.id.upload_file_button)
        imageView = findViewById(R.id.file_preview)
        galleryActivity = findViewById(R.id.gallery_activity)
        logoutButton = findViewById(R.id.logout_button)

        selectFileButton.setOnClickListener {
            openFileChooser()
        }

        uploadFileButton.setOnClickListener {
            fileUri?.let { uri ->
                uploadFileToFirebase(uri)
            } ?: run {
                Toast.makeText(this, "Nenhum arquivo selecionado!!!", Toast.LENGTH_SHORT).show()
            }
        }

        galleryActivity.setOnClickListener {
            val intent = Intent(this, GalleryActivity::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun openFileChooser() {
        val intent = Intent().apply {
            type = "*/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
        }
        startActivityForResult(intent, PICK_FILE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            val mimeType = contentResolver.getType(fileUri!!)

            if (mimeType?.startsWith("image/") == true) {
                imageView.setImageURI(fileUri)
            } else {
                imageView.setImageResource(R.drawable.baseline_wysiwyg_24)
            }
        }
    }

    private fun uploadFileToFirebase(fileUri: Uri) {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = storage.reference.child("images/$userId/")
        val mimeType = contentResolver.getType(fileUri)
        val fileExtension = when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            "application/pdf" -> ".pdf"
            "application/msword" -> ".doc"
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx"
            else -> ""
        }

        val fileRef = storageRef.child("${System.currentTimeMillis()}$fileExtension")

        val uploadTask = fileRef.putFile(fileUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Firebase", "Upload de arquivo bem-sucedido. Download URL: $uri")
                Toast.makeText(this, "Upload bem-sucedido!!!", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Upload de arquivo falhou!!!", exception)
            Toast.makeText(this, "Upload falhou!!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun logout() {
        auth.signOut()
        Toast.makeText(this, "Logout bem-sucedido", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }
}