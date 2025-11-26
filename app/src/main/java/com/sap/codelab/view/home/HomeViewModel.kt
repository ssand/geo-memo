package com.sap.codelab.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.location.GeofenceScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

/**
 * ViewModel for the Home Activity.
 */
@KoinViewModel
internal class HomeViewModel(
    private val memoRepository: MemoRepository,
    private val geofenceScheduler: GeofenceScheduler
) : ViewModel() {

    private var isShowAll = false
    private val _memos: MutableStateFlow<List<Memo>> = MutableStateFlow(listOf())
    val memos: StateFlow<List<Memo>> = _memos.asStateFlow()

    /**
     * Loads all memos.
     */
    fun loadAllMemos() {
        isShowAll = true
        viewModelScope.launch {
            _memos.value = memoRepository.getAll()
        }
    }

    /**
     * Loads all open (not done) memos.
     */
    fun loadOpenMemos() {
        isShowAll = false
        viewModelScope.launch {
            _memos.value = memoRepository.getOpen()
        }
    }

    fun refreshMemos() {
        if (isShowAll) {
            loadAllMemos()
        } else {
            loadOpenMemos()
        }
    }

    /**
     * Updates the given memo, marking it as done if isChecked is true.
     *
     * @param memo      - the memo to update.
     * @param isChecked - whether the memo has been checked (marked as done).
     */
    fun updateMemo(memo: Memo, isChecked: Boolean) = viewModelScope.launch {
        // We'll only forward the update if the memo has been checked, since we don't offer to uncheck memos right now
        if (isChecked) {
            val checkedMemo = memo.copy(isDone = true)
            memoRepository.saveMemo(checkedMemo)

            // A quick workaround to fix Memo UI state update on check.
            // A proper fix would imply some refactoring which is not in the scope of this task.
            _memos.update {
                it.toMutableList().apply {
                    indexOf(memo).takeIf { idx -> idx > 0 }?.let { idx ->
                        if (isShowAll)
                            this[idx] = checkedMemo
                        else
                            this.removeAt(idx)
                    }
                }.toList()
            }
            geofenceScheduler.removeMemoGeofence(memo.id)
        }
    }
}
