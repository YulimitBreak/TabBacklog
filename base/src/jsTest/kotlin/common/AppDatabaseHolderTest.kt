package common

import com.juul.indexeddb.Database
import com.juul.indexeddb.Key
import com.juul.indexeddb.deleteDatabase
import core.runTest
import data.database.core.*
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDate
import kotlin.test.Test

class AppDatabaseHolderTest {

    private data class Entity(val id: Int, val title: String, val date: LocalDate?)

    // If needed, add tests for:
    // * composite index
    // * autoincrement
    // * migration
    private enum class Schema(
        override val index: DbField.Index,
        override val backingField: ((Entity) -> dynamic)? = null
    ) : EntityDbField<Entity> {
        ID(DbField.Index.PrimaryKey, Entity::id),
        TITLE(DbField.Index.Field(), Entity::title),
        DATE(DbField.Index.Field(), saveAsString(Entity::date)),
        ;

        override val storeName: String = "entity"
    }

    @Test
    fun testCreation() = runTest {
        val schema = DbSchema<Schema>()
        val holder = AppDatabaseHolder(
            "test_database",
            1,
            listOf(schema)
        )
        val database = holder.database()
        onCleanup {
            database.close()
            deleteDatabase("test_database")
        }

        withClue("Can't access store name that doesn't exist") {
            checkAll<String>(10) { storeName ->
                shouldThrowAny {
                    database.transaction(storeName) {}
                }
            }
        }

        withClue("Can't access index that doesn't exist") {
            checkAll<String>(10) { indexName ->
                shouldThrowAny {
                    database.transaction(schema.storeName) {
                        objectStore(schema.storeName).index(indexName).getAll()
                    }
                }
            }
        }

        withClue("Don't have problems accessing existing indices") {

            database.transaction(schema.storeName) {
                val store = objectStore(schema.storeName)

                store.index(Schema.DATE.name).getAll()
                store.index(Schema.TITLE.name).getAll()
            }
        }
    }

    @Test
    fun testAsyncOpen() = runTest {
        val schema = DbSchema<Schema>()
        val holder = AppDatabaseHolder(
            "test_database",
            1,
            listOf(schema)
        )
        var database1: Database? = null
        var database2: Database? = null
        onCleanup {
            if (database1 != database2) {
                database2?.close()
            }
            database1?.close()
            deleteDatabase("test_database")
        }
        val job1 = launch {
            database1 = holder.database()
        }
        val job2 = launch {
            database2 = holder.database()
        }
        job2.join()
        job1.join()

        database1 shouldBeSameInstanceAs database2
    }

    @Test
    fun testReadWrite() = runTest {
        val schema = DbSchema<Schema>()
        val holder = AppDatabaseHolder(
            "test_database",
            1,
            listOf(schema)
        )
        val database = holder.database()
        onCleanup {
            database.close()
            deleteDatabase("test_database")
        }

        checkAll(10, Arb.int(), Arb.string(), Arb.int(min = -365, max = 365)) { id, title, dayDiff ->
            val entity = Entity(id, title, DateUtils.today + DatePeriod(days = dayDiff))

            database.writeTransaction(schema.storeName) {
                objectStore(schema.storeName).put(schema.generate(entity))
            }
            val newEntity = database.transaction(schema.storeName) {
                val r = objectStore(schema.storeName).get(Key(id))
                schema.extract(r) {
                    Entity(
                        Schema.ID.value(),
                        Schema.TITLE.value(),
                        Schema.DATE.value<String>().toLocalDate()
                    )
                }
            }

            newEntity shouldBe entity
        }
    }
}