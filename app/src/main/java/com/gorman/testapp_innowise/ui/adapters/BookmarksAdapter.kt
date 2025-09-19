package com.gorman.testapp_innowise.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gorman.testapp_innowise.R
import com.gorman.testapp_innowise.domain.models.Bookmark

class BookmarksAdapter : RecyclerView.Adapter<BookmarksAdapter.BookmarksViewHolder>() {

    private val bookmarksList = mutableListOf<Bookmark>()
    private var listener: OnItemClickListener? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setList(newList: List<Bookmark>) {
        bookmarksList.clear()
        bookmarksList.addAll(newList)
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

    fun getItem(position: Int): Bookmark = bookmarksList[position]

    class BookmarksViewHolder(itemView: View, listener: OnItemClickListener?) : RecyclerView.ViewHolder (itemView) {
        private val bookmarkImageView: ImageView = itemView.findViewById(R.id.bookmarkView)
        private val phName: TextView = itemView.findViewById(R.id.phName)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(position)
                }
            }
        }

        fun bind(bookmark: Bookmark) {
            Glide.with(itemView.context)
                .load(bookmark.imageUrl)
                .placeholder(R.drawable.img_placeholder)
                .error(R.drawable.img_placeholder)
                .into(bookmarkImageView)
            phName.text = bookmark.phName
        }
    }
}