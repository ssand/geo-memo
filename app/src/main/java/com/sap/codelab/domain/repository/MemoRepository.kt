package com.sap.codelab.domain.repository

import com.sap.codelab.domain.model.Memo

/**
 * Interface for a repository offering memo related CRUD operations.
 */
interface MemoRepository {

    /**
     * Saves the given memo to the database.
     */
    suspend fun saveMemo(memo: Memo): Long

    /**
     * @return all memos currently in the database.
     */
    suspend fun getAll(): List<Memo>

    /**
     * @return all memos currently in the database, except those that have been marked as "done".
     */
    suspend fun getOpen(): List<Memo>

    /**
     * @return the memo whose id matches the given id.
     */
    suspend fun getMemoById(id: Long): Memo

    /**
     * @return active memo by id.
     */
    suspend fun getActiveMemoById(id: Long): Memo

    /**
     * @return all active memos with location.
     */
    suspend fun getActiveMemosWithLocation(): List<Memo>
}
