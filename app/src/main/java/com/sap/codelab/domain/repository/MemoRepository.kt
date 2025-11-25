package com.sap.codelab.domain.repository

import com.sap.codelab.data.model.MemoEntity

/**
 * Interface for a repository offering memo related CRUD operations.
 */
interface MemoRepository {

    /**
     * Saves the given memo to the database.
     */
    fun saveMemo(memo: MemoEntity)

    /**
     * @return all memos currently in the database.
     */
    fun getAll(): List<MemoEntity>

    /**
     * @return all memos currently in the database, except those that have been marked as "done".
     */
    fun getOpen(): List<MemoEntity>

    /**
     * @return the memo whose id matches the given id.
     */
    fun getMemoById(id: Long): MemoEntity
}
