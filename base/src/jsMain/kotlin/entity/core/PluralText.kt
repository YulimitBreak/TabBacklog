package entity.core

fun interface PluralText {
    fun pluralize(count: Int): String
}

data class EnglishPlural(val singular: String, val plural: String) : PluralText {
    override fun pluralize(count: Int): String =
        if (count == 1) singular else plural
}