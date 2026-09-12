package com.example.albumassignment

import com.example.albumassignment.data.AlbumRepository
import com.example.albumassignment.ui.login.LoginUiState
import com.example.albumassignment.ui.login.LoginViewModel
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun invalidStudentIdDoesNotCallRepository() = runTest {
        val repository = mock<AlbumRepository>()
        val vm = LoginViewModel(repository)
        for (badId in listOf("", "s8092915", "809291", "809291500", "abcdefg")) {
            vm.login(badId, "TestName")
            advanceUntilIdle()
            assertEquals("Enter your student ID as 7 or 8 digits, without s.", vm.state.value.error)
        }
        verifyNoInteractions(repository)
    }

    @Test
    fun blankPasswordDoesNotCallRepository() = runTest {
        val repository = mock<AlbumRepository>()
        val vm = LoginViewModel(repository)
        vm.login("8092915", " ")
        advanceUntilIdle()
        assertEquals("Enter your first name as the password.", vm.state.value.error)
        verifyNoInteractions(repository)
    }

    @Test
    fun successfulLoginEmitsStateAndPreservesCredentials() = runTest {
        val repository = mock<AlbumRepository>()
        whenever(repository.login("8092915", "TeStName")).thenReturn("different-topic")
        val vm = LoginViewModel(repository)
        val observed = mutableListOf<LoginUiState>()
        // Start collecting immediately so this test can see the loading emission.
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.state.collect { observed.add(it) }
        }

        vm.login(" 8092915 ", "TeStName")
        assertTrue(vm.state.value.loading)
        advanceUntilIdle()

        assertEquals(listOf(false, true, false), observed.map { it.loading })
        assertEquals("different-topic", observed.last().keypass)
        assertNull(vm.state.value.error)
        verify(repository).login("8092915", "TeStName")
    }

    @Test
    fun unauthorizedLoginShowsCredentialError() = runTest {
        val repository = mock<AlbumRepository>()
        val error = HttpException(Response.error<Any>(401, "{}".toResponseBody()))
        whenever(repository.login("8092915", "WrongName")).thenThrow(error)
        val vm = LoginViewModel(repository)
        vm.login("8092915", "WrongName")
        advanceUntilIdle()
        assertTrue(vm.state.value.error.orEmpty().contains("capital letters"))
        assertNull(vm.state.value.keypass)
        assertFalse(vm.state.value.loading)
        verify(repository).login("8092915", "WrongName")
    }

    @Test
    fun networkFailureCanBeRetriedSuccessfully() = runTest {
        val repository = mock<AlbumRepository>()
        // thenAnswer can throw IOException from a Kotlin interface without @Throws.
        whenever(repository.login("8092915", "TestName"))
            .thenAnswer { throw IOException("offline") }
            .thenReturn("music")
        val vm = LoginViewModel(repository)
        vm.login("8092915", "TestName")
        advanceUntilIdle()
        assertTrue(vm.state.value.error.orEmpty().contains("internet connection"))
        assertFalse(vm.state.value.loading)

        vm.login("8092915", "TestName")
        advanceUntilIdle()
        assertEquals("music", vm.state.value.keypass)
        assertNull(vm.state.value.error)
        verify(repository, times(2)).login("8092915", "TestName")
    }

    @Test
    fun repeatedTapWhileLoadingSendsOnlyOneRequest() = runTest {
        val repository = mock<AlbumRepository>()
        whenever(repository.login("8092915", "TestName")).thenReturn("music")
        val vm = LoginViewModel(repository)
        vm.login("8092915", "TestName")
        vm.login("8092915", "TestName")
        advanceUntilIdle()
        verify(repository, times(1)).login("8092915", "TestName")
    }

    @Test
    fun consumedSuccessIsNotReplayedToANewCollector() = runTest {
        val repository = mock<AlbumRepository>()
        whenever(repository.login("8092915", "TestName")).thenReturn("music")
        val vm = LoginViewModel(repository)
        vm.login("8092915", "TestName")
        advanceUntilIdle()
        assertEquals("music", vm.state.first().keypass)

        vm.navigationHandled()

        // A new collector gets the cleared state instead of navigating again.
        assertNull(vm.state.first().keypass)
        assertFalse(vm.state.value.loading)
        verify(repository, times(1)).login("8092915", "TestName")
    }
}
