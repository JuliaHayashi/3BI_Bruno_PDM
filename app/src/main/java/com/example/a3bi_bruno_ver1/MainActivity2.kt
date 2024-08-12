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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainActivity2 : AppCompatActivity() {
    private lateinit var selectImageButton: Button
    private lateinit var uploadImageButton: Button
    private lateinit var activity3: Button
    private lateinit var imageView: ImageView
    private var imageUri: Uri? = null
    private lateinit var storage: FirebaseStorage

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        // Initialize Firebase Storage
        storage = Firebase.storage

        selectImageButton = findViewById(R.id.select_image_button)
        uploadImageButton = findViewById(R.id.upload_image_button)
        imageView = findViewById(R.id.image_view)
        activity3 = findViewById(R.id.activity3)

        selectImageButton.setOnClickListener {
            openFileChooser()
        }

        uploadImageButton.setOnClickListener {
            imageUri?.let { uri ->
                uploadImageToFirebase(uri)
            } ?: run {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        activity3.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
        }
    }


    private fun openFileChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/${fileUri.lastPathSegment}")

        val uploadTask = imageRef.putFile(fileUri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                Log.d("Firebase", "Image upload successful. Download URL: $uri")
                Toast.makeText(this, "Upload successful", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Image upload failed", exception)
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
        }
    }
}