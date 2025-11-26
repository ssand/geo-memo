package com.sap.codelab.data.mapper

import com.sap.codelab.data.model.MemoEntity
import com.sap.codelab.domain.model.Memo

/**
 * Mapper used to map data objects to business objects and vice versa.
 */
fun MemoEntity.toDomain(): Memo = Memo(
    id = id,
    title = title,
    description = description,
    reminderDate = reminderDate,
    reminderLatitude = reminderLatitude,
    reminderLongitude = reminderLongitude,
    isDone = isDone
)

fun Memo.toEntity(): MemoEntity = MemoEntity(
    id = id,
    title = title,
    description = description,
    reminderDate = reminderDate,
    reminderLatitude = reminderLatitude,
    reminderLongitude = reminderLongitude,
    isDone = isDone
)

fun List<MemoEntity>.toDomain(): List<Memo> = map(MemoEntity::toDomain)
