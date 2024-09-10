package com.example.a3bi_bruno_ver1

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ImageAdapter(private val context: Context, private val images: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view)

        init {
            // Configura o clique na imagem para baixar o arquivo
            imageView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val imageUrl = images[position]
                    downloadImage(imageUrl)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        // Carregar imagem com Glide
        Glide.with(context)
            .load(imageUrl)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    // Função para baixar a imagem usando a URL
    private fun downloadImage(imageUrl: String) {
        // Referência ao arquivo no Firebase Storage usando a URL da imagem
        val storageRef = storage.getReferenceFromUrl(imageUrl)
        // Nome do arquivo a ser baixado
        val fileName = imageUrl.substringAfterLast("/")
        // Caminho onde o arquivo será salvo localmente
        val localFile = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName)

        // Download do arquivo
        storageRef.getFile(localFile).addOnSuccessListener {
            // Sucesso no download
            Toast.makeText(context, "Download concluído: $fileName", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            // Falha no download
            Toast.makeText(context, "Falha no download", Toast.LENGTH_SHORT).show()
        }
    }
}
