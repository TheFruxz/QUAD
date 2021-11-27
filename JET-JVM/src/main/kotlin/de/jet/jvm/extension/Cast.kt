@file:Suppress("UNCHECKED_CAST")

package de.jet.jvm.extension

/**
 * Cast the given object to the given type and not highlight risky casts in the IDE.
 * @param O the type of the object to cast
 * @return the casted object
 * @author Fruxz
 * @since 1.0
 */
fun <O> Any?.forceCast() = this as O

/**
 * Cast the given object to the given type or null if fails and not highlight risky casts in the IDE.
 * @param O the type of the object to cast
 * @return the casted object
 * @author Fruxz
 * @since 1.0
 */
fun <O> Any?.forceCastOrNull() = try {
	this as O
} catch (e: ClassCastException) {
	null
}

/**
 * Cast the given object to the given type and not highlight risky casts in the IDE.
 * @param O the type of the object to cast
 * @return the casted object
 * @author Fruxz
 * @since 1.0
 */
fun <O> Any?.forceNullableCast() = this as O?

/**
 * Cast the given object to the given type or null if fails and not highlight risky casts in the IDE.
 * @param O the type of the object to cast
 * @return the casted object
 * @author Fruxz
 * @since 1.0
 */
fun <O> Any?.forceNullableCastOrNull() = try {
	this as O?
} catch (e: ClassCastException) {
	null
}

/**
 * Cast the given object to the given type and not highlight risky casts in the IDE.
 * @param O the type of the object to cast
 * @return the casted object
 * @author Fruxz
 * @since 1.0
 */
fun <O> Nothing?.forceCast() = this as O

/**
 * Cast the given object to the given type or null if fails and not highlight risky casts in the IDE.
 * @param O the type of the object to cast
 * @return the casted object
 * @author Fruxz
 * @since 1.0
 */
fun <O> Nothing?.forceCastOrNull() = try {
	this as O
} catch (e: ClassCastException) {
	null
}

/**
 * Cast the given object to the given type and not highlight risky casts in the IDE.
 * @param O the type of the object to cast
 * @return the casted object
 * @author Fruxz
 * @since 1.0
 */
fun <O> Nothing?.forceNullableCast() = this as O?

/**
 * Cast the given object to the given type or null if fails and not highlight risky casts in the IDE.
 * @param O the type of the object to cast
 * @return the casted object
 * @author Fruxz
 * @since 1.0
 */
fun <O> Nothing?.forceNullableCastOrNull() = try {
	this
} catch (e: ClassCastException) {
	null
}

/**
 * Throws away the object by returning [Unit]
 * @return [Unit]
 * @author Fruxz
 * @since 1.0
 */
fun Any?.dump() = Unit

/**
 * Throws away the nothing by returning [Unit]
 * @return [Unit]
 * @author Fruxz
 * @since 1.0
 */
fun Nothing?.dump() = Unit