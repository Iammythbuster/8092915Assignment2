package com.example.albumassignment

import com.example.albumassignment.data.AlbumRepository
import com.example.albumassignment.data.DashboardResponse
import com.example.albumassignment.ui.dashboard.DashboardUiState
import com.example.albumassignment.ui.dashboard.DashboardViewModel
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {
    @get:Rule val mainRule = MainDispatcherRule()

    @Test
    fun suppliedKeypassEmitsLoadingThenAlbumsAndApiTotal() = runTest {
        val repository = mock<AlbumRepository>()
        val response = sampleDashboard()
        whenever(repository.getDashboard("returned-from-login")).thenReturn(response)
        val vm = DashboardViewModel(repository)
        val observed = mutableListOf<DashboardUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            vm.state.collect { observed.add(it) }
        }

        vm.loadAlbums("returned-from-login")
        assertTrue(vm.state.value.loading)
        advanceUntilIdle()

        assertEquals(listOf(false, true, false), observed.map { it.loading })
        assertEquals(response.entities, observed.last().albums)
        assertEquals(response.entityTotal, vm.state.value.total)
        assertTrue(vm.state.value.loaded)
        verify(repository).getDashboard("returned-from-login")
    }

    @Test
    fun emptyResponseIsSuccessfulAndNotAnError() = runTest {
        val repository = mock<AlbumRepository>()
        whenever(repository.getDashboard("music")).thenReturn(DashboardResponse(emptyList(), 0))
        val vm = DashboardViewModel(repository)
        vm.loadAlbums("music")
        advanceUntilIdle()
        assertTrue(vm.state.value.loaded)
        assertTrue(vm.state.value.albums.isEmpty())
        assertEquals(0, vm.state.value.total)
        assertNull(vm.state.value.error)
        verify(repository).getDashboard("music")
    }

    @Test
    fun failedRequestCanBeRetried() = runTest {
        val repository = mock<AlbumRepository>()
        whenever(repository.getDashboard("music"))
            .thenAnswer { throw IOException("offline") }
            .thenReturn(sampleDashboard())
        val vm = DashboardViewModel(repository)
        vm.loadAlbums("music")
        advanceUntilIdle()
        assertTrue(vm.state.value.error.orEmpty().contains("internet connection"))
        assertFalse(vm.state.value.loading)

        vm.loadAlbums("music", forceRefresh = true)
        advanceUntilIdle()
        assertTrue(vm.state.value.loaded)
        assertNull(vm.state.value.error)
        verify(repository, times(2)).getDashboard("music")
    }

    @Test
    fun returningCollectorGetsLoadedAlbumsWithoutAnotherRequest() = runTest {
        val repository = mock<AlbumRepository>()
        val response = sampleDashboard()
        whenever(repository.getDashboard("music")).thenReturn(response)
        val vm = DashboardViewModel(repository)
        vm.loadAlbums("music")
        advanceUntilIdle()

        vm.loadAlbums("music")
        advanceUntilIdle()

        assertEquals(response.entities, vm.state.first().albums)
        verify(repository, times(1)).getDashboard("music")
    }

    @Test
    fun refreshFailureKeepsPreviouslyLoadedAlbums() = runTest {
        val repository = mock<AlbumRepository>()
        val response = sampleDashboard()
        whenever(repository.getDashboard("music"))
            .thenReturn(response)
            .thenAnswer { throw IOException("offline") }
        val vm = DashboardViewModel(repository)
        vm.loadAlbums("music")
        advanceUntilIdle()

        vm.loadAlbums("music", forceRefresh = true)
        advanceUntilIdle()

        assertEquals(response.entities, vm.state.value.albums)
        assertNotNull(vm.state.value.error)
        assertFalse(vm.state.value.loading)
        assertTrue(vm.state.value.loaded)
        verify(repository, times(2)).getDashboard("music")
    }

    @Test
    fun missingKeypassDoesNotSendRequest() = runTest {
        val repository = mock<AlbumRepository>()
        val vm = DashboardViewModel(repository)
        vm.loadAlbums("")
        advanceUntilIdle()
        assertEquals("Missing keypass. Please log in again.", vm.state.value.error)
        verifyNoInteractions(repository)
    }

    @Test
    fun repeatedLoadWhileBusySendsOneRequest() = runTest {
        val repository = mock<AlbumRepository>()
        whenever(repository.getDashboard("music")).thenReturn(sampleDashboard())
        val vm = DashboardViewModel(repository)
        vm.loadAlbums("music")
        vm.loadAlbums("music", forceRefresh = true)
        advanceUntilIdle()
        verify(repository, times(1)).getDashboard("music")
    }
}
