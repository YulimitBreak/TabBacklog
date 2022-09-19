package common

fun Any?.isNullOrEmpty(): Boolean = when (this) {
    null -> true
    is Collection<*> -> this.isNullOrEmpty()
    is Array<*> -> this.isNullOrEmpty()
    else -> {
        this == undefined || ((this is String || jsTypeOf(this).equals("string", true)) && (this.toString()
            .isEmpty() || this.toString().isBlank()))
    }
}

inline fun <reified T> Any.cast(): T = this as T

inline fun <reified T> Any.safeCast(): T? = this as? T