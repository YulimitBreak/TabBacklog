package ui.manager

import androidx.compose.runtime.*
import com.juul.indexeddb.Database
import com.juul.indexeddb.deleteDatabase
import com.juul.indexeddb.openDatabase
import com.varabyte.kobweb.compose.foundation.layout.Column
import common.DateUtils
import data.database.core.DbField
import data.database.core.DbSchema
import data.database.core.EntityDbField
import data.database.core.generate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

private var db: Database? = null

private suspend fun getDatabase(): Database {
    console.log("Initializing DB")
    db?.let { return it }
    console.log("DB not found, creating")
    val schema = DbSchema<TestSchema>()
    console.log("Deleting existing database")
    deleteDatabase("test_database")

    val database = openDatabase("test_database", 1) { database, _, _ ->
        with(schema) { createObjectStore(database) }
    }
    db = database

    console.log("database created")

    database.writeTransaction(schema.storeName) {
        console.log("Populating database")
        val store = objectStore(schema.storeName)
        store.put(schema.generate(TestData(1, "Ivan", "Petrov", null)))
        store.put(schema.generate(TestData(2, "Maria", "Petrov", null)))
        store.put(schema.generate(TestData(3, "John", "Smith", null)))
        store.put(schema.generate(TestData(4, "Jane", "Smith", null)))
        store.put(schema.generate(TestData(5, "Jim", "Jameson", DateUtils.today)))
        store.put(schema.generate(TestData(6, "Alex", "Ivanov", DateUtils.today.plus(1, DateTimeUnit.DAY))))
        store.put(schema.generate(TestData(7, "Ivan", "Ivanov", DateUtils.today.plus(3, DateTimeUnit.DAY))))
        store.put(schema.generate(TestData(8, "Ivan", "Petrov", DateUtils.today.plus(1, DateTimeUnit.WEEK))))
        store.put(schema.generate(TestData(9, "Jack", "Black", DateUtils.today.plus(1, DateTimeUnit.YEAR)).also {
            console.log(it.toString())
        }))
    }
    return database
}

@Composable
fun Manager() {

    var viewType by remember { mutableStateOf(TestViewType.GET_ALL) }
    P { Text("Manager") }

    Column {
        Div(attrs = {
            style {
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Row)
                flexWrap(FlexWrap.Wrap)
            }
        }) {
            TestViewType.values().forEach { type ->
                Button(
                    attrs = {
                        onClick {
                            viewType = type
                        }
                    }
                ) {
                    Text(type.toString())
                }
            }
        }

        TestView(viewType)
    }
}

@Composable
fun TestView(viewType: TestViewType) {
    var values by remember(viewType) { mutableStateOf(emptyList<TestData>()) }
    console.log("Recomposing testView for $viewType")
    LaunchedEffect(viewType) {
        val schema = DbSchema<TestSchema>()
        values = getDatabase().transaction(schema.storeName) {
            console.log("Starting retrieve transaction")
            val store = objectStore(schema.storeName)
            when (viewType) {
                TestViewType.GET_ALL -> {
                    store.getAll()
                }

                TestViewType.GET_ALL_SORTED_BY_NAME -> {
                    store.index(TestSchema.FirstName.name).getAll()
                }

                TestViewType.GET_ALL_SORTED_BY_DATE -> {
                    store.index(TestSchema.Date.name).getAll()
                }
            }
                .toList()
                .map {
                    schema.extract(it) { f ->
                        TestData(
                            f[TestSchema.Id] as Int,
                            f[TestSchema.FirstName] as String,
                            f[TestSchema.LastName] as String,
                            (f[TestSchema.Date] as String?)?.let { LocalDate.parse(it) },
                        )
                    }
                }
        }
    }

    values.forEach {
        P { Text(it.toString()) }
    }
}

enum class TestViewType {
    GET_ALL,
    GET_ALL_SORTED_BY_NAME,
    GET_ALL_SORTED_BY_DATE,
}

data class TestData(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val date: LocalDate?,
)

enum class TestSchema(
    override val backingField: ((TestData) -> dynamic)?, override val index: DbField.Index?
) : EntityDbField<TestData> {
    Id(TestData::id, DbField.Index.PrimaryKey),
    FirstName(TestData::firstName, DbField.Index.Field()),
    LastName(TestData::lastName, DbField.Index.Field()),
    Date({ it.date?.toString() }, DbField.Index.Field())
    ;

    override val storeName = "test_store"
}