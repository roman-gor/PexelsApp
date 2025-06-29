package com.gorman.testapp_innowise

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gorman.testapp_innowise.data.models.BookmarkImage
import com.gorman.testapp_innowise.data.models.Photo

class BookmarksAdapter : RecyclerView.Adapter<BookmarksAdapter.BookmarksViewHolder>() {

    private val bookmarksList = mutableListOf<BookmarkImage>()
    private var listener: OnItemClickListener? = null

    fun setList(newList: List<BookmarkImage>) {
        bookmarksList.clear()
        bookmarksList.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearList()
    {
        bookmarksList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookmarksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bookmark, parent, false)
        return BookmarksViewHolder(view, listener)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    override fun onBindViewHolder(holder: BookmarksViewHolder, position: Int) {
        holder.bind(bookmarksList[position])
    }

    override fun getItemCount(): Int = bookmarksList.size

    fun getItem(position: Int): BookmarkImage = bookmarksList[position]

    class BookmarksViewHolder(itemView: View, listener: OnItemClickListener?) : RecyclerView.ViewHolder (itemView) {
        private val bookmarkImageView: ImageView = itemView.findViewById<ImageView>(R.id.bookmarkView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position)
                }
            }
        }

        fun bind(bookmark: BookmarkImage) {
            Glide.with(itemView.context)
                .load(bookmark.imageUrl)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(bookmarkImageView)
        }
    }
}