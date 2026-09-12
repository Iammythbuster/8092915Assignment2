package com.example.albumassignment.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.albumassignment.MainActivity
import com.example.albumassignment.R
import com.example.albumassignment.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        binding.loginButton.setOnClickListener {
            viewModel.login(
                binding.usernameInput.text.toString(),
                binding.passwordInput.text.toString()
            )
        }

        // Collect only while this fragment view is started.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    binding.progressBar.isVisible = state.loading
                    binding.loginButton.isEnabled = !state.loading
                    binding.usernameInput.isEnabled = !state.loading
                    binding.passwordInput.isEnabled = !state.loading
                    binding.errorText.isVisible = state.error != null
                    binding.errorText.text = state.error

                    state.keypass?.let { keypass ->
                        ViewCompat.getWindowInsetsController(binding.root)
                            ?.hide(WindowInsetsCompat.Type.ime())
                        (requireActivity() as MainActivity).showDashboard(keypass)
                        viewModel.navigationHandled()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
