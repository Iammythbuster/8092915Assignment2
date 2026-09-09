package com.example.albumassignment

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.albumassignment.data.DashboardResponse
import com.example.albumassignment.ui.dashboard.DashboardViewModel
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    @get:Rule val instantRule = InstantTaskExecutorRule()
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun suppliedKeypassLoadsAlbumsAndApiTotal() = runTest {
        val fake = FakeAlbumRepository()
        val vm = DashboardViewModel(fake)
        vm.loadAlbums("returned-from-login")
        assertEquals(true, vm.state.value?.loading)
        advanceUntilIdle()
        assertEquals("returned-from-login", fake.lastKeypass)
        assertEquals(fake.dashboardResult.entities, vm.state.value?.albums)
        assertEquals(1, vm.state.value?.total)
        assertEquals(true, vm.state.value?.loaded)
        assertEquals(false, vm.state.value?.loading)
    }

    @Test
    fun emptyResponseIsSuccessfulAndNotAnError() = runTest {
        val fake = FakeAlbumRepository().apply {
            dashboardResult = DashboardResponse(emptyList(), 0)
        }
        val vm = DashboardViewModel(fake)
        vm.loadAlbums("music")
        advanceUntilIdle()
        assertEquals(true, vm.state.value?.loaded)
        assertTrue(vm.state.value!!.albums.isEmpty())
        assertNull(vm.state.value?.error)
    }

    @Test
    fun failedRequestCanBeRetried() = runTest {
        val fake = FakeAlbumRepository().apply { dashboardError = IOException("offline") }
        val vm = DashboardViewModel(fake)
        vm.loadAlbums("music")
        advanceUntilIdle()
        assertTrue(vm.state.value?.error.orEmpty().contains("internet connection"))
        assertEquals(false, vm.state.value?.loading)
        fake.dashboardError = null
        vm.loadAlbums("music", forceRefresh = true)
        advanceUntilIdle()
        assertEquals(2, fake.dashboardCalls)
        assertEquals(true, vm.state.value?.loaded)
        assertNull(vm.state.value?.error)
    }

    @Test
    fun returningToLoadedDashboardDoesNotFetchAgain() = runTest {
        val fake = FakeAlbumRepository()
        val vm = DashboardViewModel(fake)
        vm.loadAlbums("music")
        advanceUntilIdle()
        vm.loadAlbums("music")
        advanceUntilIdle()
        assertEquals(1, fake.dashboardCalls)
    }

    @Test
    fun refreshFailureKeepsPreviouslyLoadedAlbums() = runTest {
        val fake = FakeAlbumRepository()
        val vm = DashboardViewModel(fake)
        vm.loadAlbums("music")
        advanceUntilIdle()
        fake.dashboardError = IOException("offline")
        vm.loadAlbums("music", forceRefresh = true)
        advanceUntilIdle()
        assertEquals(2, fake.dashboardCalls)
        assertEquals(fake.dashboardResult.entities, vm.state.value?.albums)
        assertNotNull(vm.state.value?.error)
        assertEquals(false, vm.state.value?.loading)
    }

    @Test
    fun missingKeypassDoesNotSendRequest() = runTest {
        val fake = FakeAlbumRepository()
        val vm = DashboardViewModel(fake)
        vm.loadAlbums("")
        advanceUntilIdle()
        assertEquals(0, fake.dashboardCalls)
        assertEquals("Missing keypass. Please log in again.", vm.state.value?.error)
    }

    @Test
    fun repeatedLoadWhileBusySendsOneRequest() = runTest {
        val fake = FakeAlbumRepository()
        val vm = DashboardViewModel(fake)
        vm.loadAlbums("music")
        vm.loadAlbums("music", forceRefresh = true)
        advanceUntilIdle()
        assertEquals(1, fake.dashboardCalls)
    }
}