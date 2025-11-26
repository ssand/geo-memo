package com.sap.codelab.data.datasource.implementation

import com.sap.codelab.data.datasource.MemoDataSource
import com.sap.codelab.data.db.MemoDatabase
import com.sap.codelab.data.model.MemoEntity
import org.koin.core.annotation.Factory

/**
 * The local data source is used to retrieve data from a local database.
 * In the context of this task this is just a wrapper around the database and
 * is added for demonstration purposes.
 */
@Factory
class MemoLocalDataSource(private val database: MemoDatabase) : MemoDataSource {

    override suspend fun saveMemo(memo: MemoEntity): Long =
        database.getMemoDao().insert(memo)

    override suspend fun getOpen(): List<MemoEntity> =
        database.getMemoDao().getOpen()

    override suspend fun getAll(): List<MemoEntity> =
        database.getMemoDao().getAll()

    override suspend fun getMemoById(id: Long): MemoEntity =
        database.getMemoDao().getMemoById(id)

    override suspend fun getActiveMemoById(id: Long): MemoEntity =
        database.getMemoDao().getActiveMemoById(id)

    override suspend fun getActiveMemosWithLocation(): List<MemoEntity> =
        database.getMemoDao().getActiveMemosWithLocation()
}
