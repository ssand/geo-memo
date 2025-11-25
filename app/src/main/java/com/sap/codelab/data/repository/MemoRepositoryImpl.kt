package com.sap.codelab.data.repository

import androidx.annotation.WorkerThread
import com.sap.codelab.data.db.Database
import com.sap.codelab.data.model.MemoEntity
import com.sap.codelab.domain.repository.MemoRepository
import org.koin.core.annotation.Single

/**
 * The repository is used to retrieve data from a data source.
 */
@Single
class MemoRepositoryImpl(private val database: Database) : MemoRepository {

    @WorkerThread
    override fun saveMemo(memo: MemoEntity) {
        database.getMemoDao().insert(memo)
    }

    @WorkerThread
    override fun getOpen(): List<MemoEntity> =
        database.getMemoDao().getOpen()

    @WorkerThread
    override fun getAll(): List<MemoEntity> =
        database.getMemoDao().getAll()

    @WorkerThread
    override fun getMemoById(id: Long): MemoEntity =
        database.getMemoDao().getMemoById(id)
}
