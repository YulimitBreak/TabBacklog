import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeExactly

class TagRepoTest : FunSpec() {

    init {
        context("a = 10") {
            val a = 10
            test("a + 5 == 15") {
                a + 5 shouldBeExactly 15
            }
        }
    }
}