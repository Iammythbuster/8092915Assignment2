package com.example.albumassignment.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.albumassignment.data.AlbumRepository
import com.example.albumassignment.ui.errorMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

data class LoginUiState(
    val loading: Boolean = false,
    val error: String? = null,
    val keypass: String? = null
)

class LoginViewModel(private val repository: AlbumRepository) : ViewModel() {
    private val _state = MutableLiveData(LoginUiState())
    val state: LiveData<LoginUiState> = _state

    fun login(username: String, password: String) {
        if (_state.value?.loading == true) return
        val studentId = username.trim()

        if (!studentId.matches(Regex("[0-9]{7,8}"))) {
            _state.value = LoginUiState(error = "Enter your student ID as 7 or 8 digits, without s.")
            return
        }
        if (password.isBlank()) {
            _state.value = LoginUiState(error = "Enter your first name as the password.")
            return
        }

        _state.value = LoginUiState(loading = true)
        viewModelScope.launch {
            try {
                // Password is sent exactly as entered; capitalization is preserved.
                val keypass = repository.login(studentId, password)
                _state.value = LoginUiState(keypass = keypass)
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _state.value = LoginUiState(error = errorMessage(error, duringLogin = true))
            }
        }
    }

    fun navigationHandled() {
        // Clear success after navigation so an observer cannot navigate twice.
        _state.value = LoginUiState()
    }
}