package com.example.albumassignment.ui.details

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.albumassignment.R
import com.example.albumassignment.data.Album
import com.example.albumassignment.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // This local binding is not stored after this method finishes.
        val binding = FragmentDetailsBinding.bind(view)
        val album = requireArguments()
        binding.albumTitle.text = album.getString("albumTitle")
        binding.artistName.text = getString(R.string.artist_value, album.getString("artistName"))
        binding.releaseYear.text = getString(R.string.release_year_value, album.getInt("releaseYear"))
        binding.genre.text = getString(R.string.genre_value, album.getString("genre"))
        binding.trackCount.text = getString(R.string.track_count_value, album.getInt("trackCount"))
        binding.popularTrack.text = getString(R.string.popular_track_value, album.getString("popularTrack"))
        binding.description.text = album.getString("description")
        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(album: Album) = DetailsFragment().apply {
            // Primitive Bundle values survive normal fragment state restoration.
            arguments = bundleOf(
                "artistName" to album.artistName,
                "albumTitle" to album.albumTitle,
                "releaseYear" to album.releaseYear,
                "genre" to album.genre,
                "trackCount" to album.trackCount,
                "popularTrack" to album.popularTrack,
                "description" to album.description
            )
        }
    }
}