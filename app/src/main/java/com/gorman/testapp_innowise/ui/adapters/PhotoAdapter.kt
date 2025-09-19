package com.gorman.testapp_innowise.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gorman.testapp_innowise.R
import com.gorman.testapp_innowise.domain.models.Photo

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    private val photoList = mutableListOf<Photo>()
    private var listener: OnItemClickListener? = null

    @SuppressLint("NotifyDataSetChanged")
    fun appendList(list: List<Photo>)
    {
        val existingUrls = photoList.map { it.src.large }.toSet()
        val filteredList = list.filter { it.src.large !in existingUrls }
        if (filteredList.isEmpty()) return
        val start = photoList.size
        photoList.addAll(filteredList)
        notifyItemRangeInserted(start, filteredList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearList()
    {
        photoList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view, listener)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photoList[position])
    }

    override fun getItemCount(): Int = photoList.size

    fun getItem(position: Int): Photo = photoList[position]

    class PhotoViewHolder(itemView: View, listener: OnItemClickListener?) : RecyclerView.ViewHolder (itemView) {
        private val photoImageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position)
                }
            }
        }

        fun bind(photo: Photo) {
            Glide.with(itemView.context)
                .load(photo.src.large)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(photoImageView)
        }
    }
}