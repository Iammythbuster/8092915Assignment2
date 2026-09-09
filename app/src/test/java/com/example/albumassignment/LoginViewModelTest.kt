package com.example.albumassignment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.albumassignment.ui.login.LoginViewModel
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule val instantRule = InstantTaskExecutorRule()
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun invalidStudentIdDoesNotCallRepository() = runTest {
        val fake = FakeAlbumRepository()
        val vm = LoginViewModel(fake)
        for (badId in listOf("", "s8092915", "809291", "809291500", "abcdefg")) {
            vm.login(badId, "TestName")
            advanceUntilIdle()
            assertEquals(0, fake.loginCalls)
            assertEquals("Enter your student ID as 7 or 8 digits, without s.", vm.state.value?.error)
        }
    }

    @Test
    fun blankPasswordDoesNotCallRepository() = runTest {
        val fake = FakeAlbumRepository()
        val vm = LoginViewModel(fake)
        vm.login("8092915", " ")
        advanceUntilIdle()
        assertEquals(0, fake.loginCalls)
        assertEquals("Enter your first name as the password.", vm.state.value?.error)
    }

    @Test
    fun successfulLoginPreservesPasswordAndReturnsActualKeypass() = runTest {
        val fake = FakeAlbumRepository().apply { loginResult = "different-topic" }
        val vm = LoginViewModel(fake)
        vm.login(" 8092915 ", "TeStName")
        assertEquals(true, vm.state.value?.loading)
        advanceUntilIdle()
        assertEquals("8092915", fake.lastUsername)
        assertEquals("TeStName", fake.lastPassword)
        assertEquals("different-topic", vm.state.value?.keypass)
        assertEquals(false, vm.state.value?.loading)
        assertNull(vm.state.value?.error)
    }

    @Test
    fun unauthorizedLoginShowsCredentialError() = runTest {
        val fake = FakeAlbumRepository().apply {
            loginError = HttpException(Response.error<Any>(401, "{}".toResponseBody()))
        }
        val vm = LoginViewModel(fake)
        vm.login("8092915", "WrongName")
        advanceUntilIdle()
        assertTrue(vm.state.value?.error.orEmpty().contains("capital letters"))
        assertNull(vm.state.value?.keypass)
        assertEquals(false, vm.state.value?.loading)
    }

    @Test
    fun networkFailureCanBeRetriedSuccessfully() = runTest {
        val fake = FakeAlbumRepository().apply { loginError = IOException("offline") }
        val vm = LoginViewModel(fake)
        vm.login("8092915", "TestName")
        advanceUntilIdle()
        assertTrue(vm.state.value?.error.orEmpty().contains("internet connection"))
        assertEquals(false, vm.state.value?.loading)
        fake.loginError = null
        vm.login("8092915", "TestName")
        advanceUntilIdle()
        assertEquals("music", vm.state.value?.keypass)
        assertNull(vm.state.value?.error)
    }

    @Test
    fun repeatedTapWhileLoadingSendsOnlyOneRequest() = runTest {
        val fake = FakeAlbumRepository()
        val vm = LoginViewModel(fake)
        vm.login("8092915", "TestName")
        vm.login("8092915", "TestName")
        advanceUntilIdle()
        assertEquals(1, fake.loginCalls)
    }

    @Test
    fun consumedSuccessCannotTriggerNavigationAgain() = runTest {
        val vm = LoginViewModel(FakeAlbumRepository())
        vm.login("8092915", "TestName")
        advanceUntilIdle()
        vm.navigationHandled()
        assertNull(vm.state.value?.keypass)
    }
}