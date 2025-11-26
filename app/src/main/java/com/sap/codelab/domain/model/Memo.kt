package com.sap.codelab.domain.model

/**
 * Represents the Memo business object
 */
data class Memo(
    val id: Long = -1L,
    val title: String? = null,
    val description: String? = null,
    val reminderDate: Long = 0L,
    val reminderLatitude: Double? = null,
    val reminderLongitude: Double? = null,
    val isDone: Boolean = false
)
