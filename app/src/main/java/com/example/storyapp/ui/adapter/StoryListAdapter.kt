package com.example.storyapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.local.room.entity.StoryEntity
import com.example.storyapp.databinding.ItemStoryBinding

class StoryListAdapter :
    PagingDataAdapter<StoryEntity, StoryListAdapter.ListViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryEntity)
    }

    inner class ListViewHolder(private var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryEntity) {
            var isFavorite = false


            binding.ivIconFavorite.setOnClickListener{
                isFavorite = !isFavorite
                if (isFavorite) {
                    binding.ivIconFavorite.setImageResource(R.drawable.ic_favorite_filled) // Ganti ke ikon aktif
                } else {
                    binding.ivIconFavorite.setImageResource(R.drawable.ic_favorite_border) // Ganti ke ikon aktif
            }
                }


            binding.tvAuthorName.text = "From: ${data.name}"
            binding.ivIconMore
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .into(binding.ivListStory)

            binding.ivIconMore.setImageResource(R.drawable.ic_more_horizontal)
            binding.ivIconFavorite.setImageResource(if(binding.ivIconFavorite.isClickable) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border)

            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
        holder.itemView.setOnClickListener {
            getItem(position)?.let { it1 -> onItemClickCallback.onItemClicked(it1) }
        }
    }

    private fun setMessage(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }
        }
    }

}
