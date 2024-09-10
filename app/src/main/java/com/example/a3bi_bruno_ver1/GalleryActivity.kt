package com.example.a3bi_bruno_ver1

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class GalleryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private val imageLinks = mutableListOf<String>()
    private lateinit var storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        recyclerView = findViewById(R.id.recycler_view)
        storage = Firebase.storage
        auth = Firebase.auth

        recyclerView.layoutManager = LinearLayoutManager(this)
        imageAdapter = ImageAdapter(this, imageLinks)
        recyclerView.adapter = imageAdapter

        loadImagesFromFirebase()
    }

    private fun loadImagesFromFirebase() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = storage.reference.child("images/$userId/")
        storageRef.listAll().addOnSuccessListener { result ->
            imageLinks.clear()
            val downloadUrls = mutableListOf<String>()
            if (result.items.isEmpty()) {
                Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            for (fileRef in result.items) {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    downloadUrls.add(uri.toString())
                    if (downloadUrls.size == result.items.size) {
                        imageLinks.addAll(downloadUrls)
                        imageAdapter.notifyDataSetChanged()
                    }
                }.addOnFailureListener {
                    Log.e("Firebase", "Failed to get download URL for ${fileRef.name}", it)
                }
            }
        }.addOnFailureListener {
            Log.e("Firebase", "Failed to list files", it)
            Toast.makeText(this, "Failed to load images", Toast.LENGTH_SHORT).show()
        }
    }
}
