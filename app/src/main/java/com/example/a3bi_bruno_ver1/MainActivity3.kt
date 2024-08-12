package com.example.a3bi_bruno_ver1

import ImageAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class MainActivity3 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private val imageLinks = mutableListOf<String>()
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        recyclerView = findViewById(R.id.recycler_view)
        storage = Firebase.storage

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        imageAdapter = ImageAdapter(this, imageLinks)
        recyclerView.adapter = imageAdapter

        // Carregar imagens do Firebase Storage
        loadImagesFromFirebase()
    }

    private fun loadImagesFromFirebase() {
        val storageRef = storage.reference.child("images/")
        storageRef.listAll().addOnSuccessListener { result ->
            imageLinks.clear()
            val downloadUrls = mutableListOf<String>()
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
