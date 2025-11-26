package com.sap.codelab.view.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.location.GeofenceScheduler
import com.sap.codelab.utils.ifLet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

/**
 * ViewModel for matching CreateMemo view. Handles user interactions and
 * persist UI state across configuration changes.
 * [androidx.lifecycle.SavedStateHandle] should be used to persist the state on process death, although in the
 * current implementation app's backstack is recreated on process death which defies the purpose of the saved state.
 */
@KoinViewModel
internal class CreateMemoViewModel(
    private val memoRepository: MemoRepository,
    private val geofenceScheduler: GeofenceScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateMemoUiState())
    val uiState: StateFlow<CreateMemoUiState> = _uiState.asStateFlow()

    /**
     * Saves the memo in it's current state.
     * If Lat and Long are provided a geofence is registered.
     */
    fun saveMemo(onSaved: () -> Unit) = viewModelScope.launch {
        val memoId = memoRepository.saveMemo(currentMemo())
        _uiState.update { it.copy(id = memoId) }
        with(currentMemo()) {
            ifLet(reminderLatitude, reminderLongitude) { (lat, long) ->
                geofenceScheduler.registerMemoGeofence(this.id, lat, long)
            }
        }
        onSaved()
    }

    fun updateMemoData(title: String, description: String) =
        _uiState.update { it.copy(title = title, description = description) }

    fun updateLocation(latitude: Double, longitude: Double) =
        _uiState.update { it.copy(reminderLatitude = latitude, reminderLongitude = longitude) }

    fun validateInput(): Boolean {
        var isValid = true
        _uiState.update {
            val titleError = it.title.isBlank()
            val descriptionError = it.description.isBlank()
            isValid = !titleError && !descriptionError
            it.copy(titleError = titleError, descriptionError = descriptionError)
        }
        return isValid
    }

    fun hasLocation(): Boolean =
        uiState.value.hasLocation

    fun currentMemo(): Memo =
        uiState.value.toMemo()
}

/**
 * UI state for the CreateMemo view.
 */
internal data class CreateMemoUiState(
    val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    val reminderLatitude: Double = 0.0,
    val reminderLongitude: Double = 0.0,
    val titleError: Boolean = false,
    val descriptionError: Boolean = false
) {
    val hasLocation: Boolean
        get() = reminderLatitude != 0.0 || reminderLongitude != 0.0

    fun toMemo(): Memo = Memo(
        id = id,
        title = title,
        description = description,
        reminderDate = 0L,
        reminderLatitude = reminderLatitude,
        reminderLongitude = reminderLongitude,
        isDone = false
    )
}
