package com.example.lab2pluis

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView


class GalleryAdapter(
    private val galleryList: MutableList<GalleryImage>
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.ivImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):GalleryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return GalleryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val galleryImage = galleryList[position]
        val image = galleryImage.uri

        with(holder) {
            imageView.setImageURI(image)

            imageView.setOnClickListener {
                openFullImage(holder.imageView.context, galleryImage, position)
            }
        }

    }

    override fun getItemCount() = galleryList.size

    private fun openFullImage(context: Context, galleryImage: GalleryImage, position: Int) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.full_image, null)

        val ivImage = dialogView.findViewById<ImageView>(R.id.ivFullImage)
        val btnDelite = dialogView.findViewById<Button>(R.id.btnDeleteImage)

        ivImage.setImageURI(galleryImage.uri)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnDelite.setOnClickListener {
            galleryList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, galleryList.size)
            dialog.dismiss()
        }

        dialog.show()

    }

}