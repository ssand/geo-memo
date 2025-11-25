package com.sap.codelab.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sap.codelab.data.model.MemoEntity

/**
 * That database that is used to store information.
 */
@Database(
    entities = [MemoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {

    abstract fun getMemoDao(): MemoDao
}
