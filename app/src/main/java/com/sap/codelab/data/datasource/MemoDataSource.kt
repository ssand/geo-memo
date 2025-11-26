package com.sap.codelab.data.datasource

import com.sap.codelab.data.model.MemoEntity

interface MemoDataSource {

    /**
     * Saves the given memo to the database.
     */
    suspend fun saveMemo(memo: MemoEntity): Long

    /**
     * @return all memos currently in the database.
     */
    suspend fun getAll(): List<MemoEntity>

    /**
     * @return all memos currently in the database, except those that have been marked as "done".
     */
    suspend fun getOpen(): List<MemoEntity>

    /**
     * @return the memo whose id matches the given id.
     */
    suspend fun getMemoById(id: Long): MemoEntity

    /**
     * @return active memo by id.
     */
    suspend fun getActiveMemoById(id: Long): MemoEntity

    /**
     * @return all active memos with location.
     */
    suspend fun getActiveMemosWithLocation(): List<MemoEntity>
}
