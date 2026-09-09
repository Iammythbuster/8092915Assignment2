package com.example.albumassignment.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.albumassignment.R
import com.example.albumassignment.data.Album
import com.example.albumassignment.databinding.ItemAlbumBinding

class AlbumAdapter(private val onClick: (Album) -> Unit) :
    ListAdapter<Album, AlbumAdapter.AlbumViewHolder>(AlbumDiffCallback) {

    inner class AlbumViewHolder(private val binding: ItemAlbumBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            val context = binding.root.context
            binding.albumTitle.text = album.albumTitle
            binding.artistName.text = album.artistName
            binding.albumMeta.text = context.getString(
                R.string.album_meta, album.releaseYear, album.genre, album.trackCount
            )
            binding.popularTrack.text = context.getString(
                R.string.popular_track_value, album.popularTrack
            )
            binding.root.setOnClickListener { onClick(album) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private object AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.artistName == newItem.artistName &&
                    oldItem.albumTitle == newItem.albumTitle
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }
    }
}