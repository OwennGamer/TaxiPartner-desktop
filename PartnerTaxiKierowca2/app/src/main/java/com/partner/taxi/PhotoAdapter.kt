package com.partner.taxi

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PhotoAdapter(
    private val photos: MutableList<Uri>,
    private val onAdd: () -> Unit,
    private val onClick: (Uri) -> Unit,
    private val onLongClick: (Int) -> Unit
) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(v)
    }

    override fun getItemCount(): Int = photos.size + 1

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        if (position < photos.size) {
            val uri = photos[position]
            Glide.with(holder.img.context).load(uri).centerCrop().into(holder.img)
            holder.img.setOnClickListener { onClick(uri) }
            holder.img.setOnLongClickListener {
                onLongClick(position)
                true
            }
        } else {
            holder.img.setImageResource(android.R.drawable.ic_input_add)
            holder.img.scaleType = ImageView.ScaleType.CENTER
            holder.img.setOnClickListener { onAdd() }
            holder.img.setOnLongClickListener(null)
        }
    }
}