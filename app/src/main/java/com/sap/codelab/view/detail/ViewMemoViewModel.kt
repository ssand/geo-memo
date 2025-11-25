package com.sap.codelab.view.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.data.model.MemoEntity
import com.sap.codelab.domain.repository.MemoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

/**
 * ViewModel for matching ViewMemo view.
 */
@Factory
internal class ViewMemoViewModel(private val memoRepository: MemoRepository) : ViewModel() {

    private val _memo: MutableStateFlow<MemoEntity?> = MutableStateFlow(null)
    val memo: StateFlow<MemoEntity?> = _memo

    /**
     * Loads the memo whose id matches the given memoId from the database.
     */
    fun loadMemo(memoId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            _memo.value = memoRepository.getMemoById(memoId)
        }
    }
}
