import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.a3bi_bruno_ver1.R

//Adaptador no RecyclerView para exibir uma lista de URL
//Biblioteca Glide para exibir as imagens usando URLs
class ImageAdapter(private val context: Context, private val images: List<String>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    //referência ao ImageView do xml
    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_gallery, parent, false)
        return ImageViewHolder(view)
    }

    //coloca imagem nas posições do RecyclerView
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = images[position]
        Glide.with(context)
            .load(imageUrl)
            .into(holder.imageView)
    }

    //número total de imagens
    override fun getItemCount(): Int {
        return images.size
    }
}
