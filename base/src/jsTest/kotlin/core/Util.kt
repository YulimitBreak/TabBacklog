package core

import io.kotest.property.Arb
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.shuffle

fun <T> Arb<List<T>>.shuffle() = this.flatMap { Arb.shuffle(it) }