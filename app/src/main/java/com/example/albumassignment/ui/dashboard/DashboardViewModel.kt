package com.example.albumassignment.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumassignment.data.Album
import com.example.albumassignment.data.AlbumRepository
import com.example.albumassignment.ui.errorMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val loading: Boolean = false,
    val albums: List<Album> = emptyList(),
    val total: Int = 0,
    val error: String? = null,
    val loaded: Boolean = false
)

class DashboardViewModel(private val repository: AlbumRepository) : ViewModel() {
    private val _state = MutableStateFlow(DashboardUiState())
    val state: StateFlow<DashboardUiState> = _state.asStateFlow()

    fun loadAlbums(keypass: String, forceRefresh: Boolean = false) {
        val current = _state.value
        if (current.loading || (current.loaded && !forceRefresh)) return
        if (keypass.isBlank()) {
            _state.value = DashboardUiState(error = "Missing keypass. Please log in again.")
            return
        }

        // Preserve the current list if a later refresh fails.
        _state.value = current.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val response = repository.getDashboard(keypass)
                _state.value = DashboardUiState(
                    albums = response.entities,
                    total = response.entityTotal,
                    loaded = true
                )
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.value = current.copy(error = errorMessage(error))
            }
        }
    }
}
