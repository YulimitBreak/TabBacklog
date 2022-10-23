import com.juul.indexeddb.Cursor
import data.database.core.DbField
import data.database.core.DbSchema
import data.database.core.paginate
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.flow.toList
import kotlin.test.Test

class PaginateTest {

    enum class Schema(override val index: DbField.Index?) : DbField {
        ID(DbField.Index.PrimaryKey),
        STRING(DbField.Index.Field())
        ;

        override val storeName: String = "strings"
    }

    @Test
    fun paginate_primary() = runTest {

        val dbSize = 20
        val holder = setupDatabase(dbSize)

        withClue("Forwards pagination") {
            checkAll(20, Arb.int(min = dbSize / 4, max = dbSize * 2)) { pageSize ->

                val schema = DbSchema<Schema>()
                val result = holder.database()
                    .paginate(schema.storeName, cursorDirection = Cursor.Direction.Next, pageSize = 5) {
                        schema.extract<Int>(it.value, Schema.ID)
                    }.toList()
                result shouldHaveSize dbSize
                result shouldBeSortedWith { a, b -> a.compareTo(b) }
            }
        }

        withClue("Backwards pagination") {
            checkAll(20, Arb.int(min = dbSize / 4, max = dbSize * 2)) { pageSize ->

                val schema = DbSchema<Schema>()
                val result = holder.database()
                    .paginate(schema.storeName, cursorDirection = Cursor.Direction.Previous, pageSize = 5) {
                        schema.extract<Int>(it.value, Schema.ID)
                    }.toList()
                result shouldHaveSize dbSize
                result shouldBeSortedWith { a, b -> -a.compareTo(b) }
            }
        }
    }

    private suspend fun TestScope.setupDatabase(size: Int): TestDatabaseHolder {
        val holder = TestDatabaseHolder("paginate_test_database_size_$size", listOf(DbSchema<PaginateTest.Schema>()))
        onCleanup {
            holder.deleteDatabase()
        }
        val database = holder.database()
        val schema = DbSchema<PaginateTest.Schema>()
        database.writeTransaction(schema.storeName) {
            val strings = Arb.string(minSize = 2).samples().take(size).map { it.value }
            val store = objectStore(schema.storeName)
            strings.forEachIndexed { index, s ->
                store.put(schema.generate(mapOf(PaginateTest.Schema.STRING to s, Schema.ID to index)))
            }
        }
        return holder
    }
}