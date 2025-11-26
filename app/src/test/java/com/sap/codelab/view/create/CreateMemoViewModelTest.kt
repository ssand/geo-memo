package com.sap.codelab.view.create

import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.location.GeofenceScheduler
import com.sap.codelab.util.TestDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CreateMemoViewModelTest {

    @get:Rule
    val dispatcherRule = TestDispatcherRule()
    private val repository: MemoRepository = mockk()
    private val geofenceScheduler: GeofenceScheduler = mockk(relaxed = true)

    @Test
    fun `validateInput marks errors when empty`() = runTest {
        val viewModel = CreateMemoViewModel(repository, geofenceScheduler)

        val isValid = viewModel.validateInput()

        assertFalse(isValid)
        assertTrue(viewModel.uiState.value.titleError)
        assertTrue(viewModel.uiState.value.descriptionError)
    }

    @Test
    fun `validateInput passes when fields filled`() = runTest {
        val viewModel = CreateMemoViewModel(repository, geofenceScheduler)
        viewModel.updateMemoData("Some title", "Some description")

        val isValid = viewModel.validateInput()

        assertTrue(isValid)
        assertFalse(viewModel.uiState.value.titleError)
        assertFalse(viewModel.uiState.value.descriptionError)
    }

    @Test
    fun `saveMemo persists memo updates id and registers geofence when location present`() = runTest {
        val viewModel = CreateMemoViewModel(repository, geofenceScheduler)
        viewModel.updateMemoData("Title", "Description")
        viewModel.updateLocation(1.0, 2.0)
        coEvery { repository.saveMemo(any()) } returns 42L
        every { geofenceScheduler.registerMemoGeofence(any(), any(), any()) } returns Unit
        var callbackInvoked = false

        viewModel.saveMemo { callbackInvoked = true }
        testScheduler.advanceUntilIdle()

        coVerify {
            repository.saveMemo(
                Memo(
                    id = 0L,
                    title = "Title",
                    description = "Description",
                    reminderDate = 0L,
                    reminderLatitude = 1.0,
                    reminderLongitude = 2.0,
                    isDone = false
                )
            )
        }
        verify(exactly = 1) { geofenceScheduler.registerMemoGeofence(42L, 1.0, 2.0) }
        assertTrue(callbackInvoked)
        assertEquals(42L, viewModel.uiState.value.id)
    }

    @Test
    fun `saveMemo register geofence when no location`() = runTest {
        val viewModel = CreateMemoViewModel(repository, geofenceScheduler)
        viewModel.updateMemoData("Title", "Description")
        coEvery { repository.saveMemo(any()) } returns 1L

        viewModel.saveMemo { }
        testScheduler.advanceUntilIdle()

        verify(exactly = 1) { geofenceScheduler.registerMemoGeofence(any(), any(), any()) }
    }
}
