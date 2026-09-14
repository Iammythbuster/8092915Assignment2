package com.example.albumassignment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.albumassignment.data.Album
import com.example.albumassignment.databinding.ActivityMainBinding
import com.example.albumassignment.ui.dashboard.DashboardFragment
import com.example.albumassignment.ui.details.DetailsFragment
import com.example.albumassignment.ui.login.LoginFragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Keep content clear of system bars and the on-screen keyboard.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val space = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime()
            )
            view.setPadding(space.left, space.top, space.right, space.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragmentContainer, LoginFragment())
            }
        }
    }

    fun showDashboard(keypass: String) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            // Fade between screens using the two small animator XML files.
            setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
            replace(R.id.fragmentContainer, DashboardFragment.newInstance(keypass))
        }
    }

    fun showDetails(album: Album) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            setCustomAnimations(
                R.animator.fade_in, R.animator.fade_out, // opening Details
                R.animator.fade_in, R.animator.fade_out  // returning to Dashboard
            )
            replace(R.id.fragmentContainer, DetailsFragment.newInstance(album))
            addToBackStack("details")
        }
    }
    fun signOut() {
        val manager = supportFragmentManager

        if (manager.isStateSaved) return

        manager.popBackStackImmediate(
            null,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )

        manager.commit {
            setReorderingAllowed(true)

            setCustomAnimations(
                R.animator.fade_in,
                R.animator.fade_out
            )

            replace(R.id.fragmentContainer, LoginFragment())
        }
    }
}
