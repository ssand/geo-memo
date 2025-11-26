package com.sap.codelab.data.repository

import com.sap.codelab.data.datasource.MemoDataSource
import com.sap.codelab.data.mapper.toDomain
import com.sap.codelab.data.mapper.toEntity
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.domain.repository.MemoRepository
import org.koin.core.annotation.Factory

/**
 * The repository is used to retrieve data from a data source.
 */
@Factory
class MemoRepositoryImpl(private val localDs: MemoDataSource) : MemoRepository {

    override suspend fun saveMemo(memo: Memo): Long =
        localDs.saveMemo(memo.toEntity())

    override suspend fun getOpen(): List<Memo> =
        localDs.getOpen().toDomain()

    override suspend fun getAll(): List<Memo> =
        localDs.getAll().toDomain()

    override suspend fun getMemoById(id: Long): Memo =
        localDs.getMemoById(id).toDomain()

    override suspend fun getActiveMemoById(id: Long): Memo =
        localDs.getActiveMemoById(id).toDomain()

    override suspend fun getActiveMemosWithLocation(): List<Memo> =
        localDs.getActiveMemosWithLocation().toDomain()
}
