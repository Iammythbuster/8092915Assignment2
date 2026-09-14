package com.example.albumassignment.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.albumassignment.MainActivity
import com.example.albumassignment.R
import com.example.albumassignment.databinding.FragmentDashboardBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDashboardBinding.bind(view)
        val keypass = requireArguments().getString(KEYPASS).orEmpty()
        var openingDetails = false
        val albumAdapter = AlbumAdapter { album ->
            if (!openingDetails) {
                openingDetails = true
                (requireActivity() as MainActivity).showDetails(album)
            }
        }
        binding.albumList.layoutManager = LinearLayoutManager(requireContext())
        binding.albumList.adapter = albumAdapter

        binding.refreshButton.setOnClickListener {
            viewModel.loadAlbums(keypass, forceRefresh = true)
        }
        binding.signOutButton.setOnClickListener {
            if (!openingDetails) {
                openingDetails = true

                binding.signOutButton.isEnabled = false
                binding.refreshButton.isEnabled = false

                (requireActivity() as MainActivity).signOut()
            }
        }
        // Collect only while this fragment view is started.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progressBar.isVisible = state.loading
                    binding.refreshButton.isEnabled = !state.loading
                    binding.refreshButton.setText(
                        if (state.error != null) R.string.retry else R.string.refresh
                    )
                    binding.errorText.isVisible = state.error != null
                    binding.errorText.text = state.error
                    binding.emptyText.isVisible = state.loaded && state.albums.isEmpty() &&
                        !state.loading && state.error == null
                    binding.albumCount.text = getString(R.string.album_count, state.total)
                    albumAdapter.submitList(state.albums)
                }
            }
        }
        viewModel.loadAlbums(keypass)
    }

    override fun onDestroyView() {
        binding.albumList.adapter = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val KEYPASS = "keypass"

        fun newInstance(keypass: String) = DashboardFragment().apply {
            arguments = bundleOf(KEYPASS to keypass)
        }
    }
}
