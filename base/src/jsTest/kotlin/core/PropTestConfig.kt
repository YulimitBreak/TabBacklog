package core

import io.kotest.common.ExperimentalKotest
import io.kotest.property.Constraints
import io.kotest.property.PropTestConfig
import io.kotest.property.and
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalKotest::class)
fun PropTestConfig.limit(duration: Duration? = null, iterations: Int? = null): PropTestConfig {
    when {
        duration != null && iterations != null -> Constraints.duration(duration).and(Constraints.iterations(iterations))
        duration != null -> Constraints.duration(duration)
        iterations != null -> Constraints.iterations(iterations)
        else -> return this
    }.let {
        return this.copy(constraints = constraints?.and(it) ?: it)
    }
}

val timeLimit get() = timeLimit(1.seconds)

fun timeLimit(duration: Duration) = PropTestConfig(constraints = Constraints.duration(duration))