package com.sap.codelab.view.create

import androidx.lifecycle.ViewModel
import com.sap.codelab.data.model.MemoEntity
import com.sap.codelab.domain.repository.MemoRepository
import com.sap.codelab.utils.coroutines.ScopeProvider
import com.sap.codelab.utils.extensions.empty
import kotlinx.coroutines.launch
import org.koin.core.annotation.Factory

/**
 * ViewModel for matching CreateMemo view. Handles user interactions.
 */
@Factory
internal class CreateMemoViewModel(private val memoRepository: MemoRepository) : ViewModel() {

    private var memo = MemoEntity(0, String.empty(), String.empty(), 0, 0, 0, false)

    /**
     * Saves the memo in it's current state.
     */
    fun saveMemo() {
        // todo check if needed
        ScopeProvider.application.launch {
            memoRepository.saveMemo(memo)
        }
    }

    /**
     * Call this method to update the memo. This is usually needed when the user changed his input.
     */
    fun updateMemo(title: String, description: String) {
        memo = MemoEntity(title = title, description = description, id = 0, reminderDate = 0, reminderLatitude = 0, reminderLongitude = 0, isDone = false)
    }

    /**
     * @return true if the title and content are not blank; false otherwise.
     */
    fun isMemoValid(): Boolean = memo.title.isNotBlank() && memo.description.isNotBlank()

    /**
     * @return true if the memo text is blank, false otherwise.
     */
    fun hasTextError() = memo.description.isBlank()

    /**
     * @return true if the memo title is blank, false otherwise.
     */
    fun hasTitleError() = memo.title.isBlank()
}
