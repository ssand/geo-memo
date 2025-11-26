package com.sap.codelab.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sap.codelab.utils.extensions.empty

/**
 * Represents a locally stored data object for a Memo.
 */
@Entity(tableName = "memo")
data class MemoEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(name = "title")
    var title: String?,
    @ColumnInfo(name = "description")
    var description: String?,
    @ColumnInfo(name = "reminderDate")
    var reminderDate: Long,
    @ColumnInfo(name = "reminderLatitude")
    var reminderLatitude: Double?,
    @ColumnInfo(name = "reminderLongitude")
    var reminderLongitude: Double?,
    @ColumnInfo(name = "isDone")
    var isDone: Boolean = false
) {
    companion object {
        fun getDummyMemo() =
            MemoEntity(
                0,
                String.empty(),
                String.empty(),
                0,
                0.0,
                0.0,
                false
            )

    }
}
