package com.example.albumassignment.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.albumassignment.data.Album
import com.example.albumassignment.data.displaySummary
import com.example.albumassignment.data.displayTitle
import com.example.albumassignment.databinding.ItemAlbumBinding

class AlbumAdapter(private val onClick: (Album) -> Unit) :
    ListAdapter<Album, AlbumAdapter.AlbumViewHolder>(ItemDiffCallback) {

    inner class AlbumViewHolder(
        private val binding: ItemAlbumBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Album) {
            binding.albumTitle.text = item.displayTitle()
            binding.albumMeta.text = item.displaySummary()

            // These fixed music fields are no longer needed.
            binding.artistName.isVisible = false
            binding.popularTrack.isVisible = false

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlbumViewHolder {
        val binding = ItemAlbumBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AlbumViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    private object ItemDiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(
            oldItem: Album,
            newItem: Album
        ): Boolean {
            // No universal unique ID is supplied for every topic.
            // Changed items are treated as replacements.
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: Album,
            newItem: Album
        ): Boolean {
            return oldItem == newItem
        }
    }
}