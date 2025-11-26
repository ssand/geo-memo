package com.sap.codelab.utils

/**
 * Same as let but checks as many values as needed for nullness.
 *
 * @param elements the elements to check for nullness
 * @param closure the closure to execute if all elements are not null
 */
inline fun <T : Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
    elements.filterNotNull().takeIf { it.size == elements.size }?.let(closure)
}
