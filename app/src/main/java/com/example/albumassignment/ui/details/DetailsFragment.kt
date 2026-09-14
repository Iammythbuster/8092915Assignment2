package com.example.albumassignment.ui.details

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.albumassignment.R
import com.example.albumassignment.data.Album
import com.example.albumassignment.data.displayDescription
import com.example.albumassignment.data.displaySummary
import com.example.albumassignment.data.displayTitle
import com.example.albumassignment.databinding.FragmentDetailsBinding

class DetailsFragment : Fragment(R.layout.fragment_details) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDetailsBinding.bind(view)
        val args = requireArguments()

        binding.albumTitle.text = args.getString("title", "Item")

        // Reuse the existing TextView for all summary fields.
        binding.releaseYear.text = args.getString("summary", "")

        binding.artistName.isVisible = false
        binding.genre.isVisible = false
        binding.trackCount.isVisible = false
        binding.popularTrack.isVisible = false

        binding.description.text = args.getString("description")
            ?.takeIf { it.isNotBlank() }
            ?: "No description provided."

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(item: Album) = DetailsFragment().apply {
            arguments = bundleOf(
                "title" to item.displayTitle(),
                "summary" to item.displaySummary(),
                "description" to item.displayDescription()
            )
        }
    }
}