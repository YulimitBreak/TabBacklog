package repository.tags

import com.juul.indexeddb.Key
import core.TestDatabaseHolder
import core.onCleanup
import core.runTest
import core.timeLimit
import data.TagRepository
import data.database.core.DatabaseHolder
import data.database.core.DbSchema
import data.database.schema.TagCountSchema
import data.database.schema.TagSchema
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldBeSortedWith
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.string.shouldStartWith
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TagRepositoryTest {

    private val tagSchema = DbSchema<TagSchema>()
    private val tagCountSchema = DbSchema<TagCountSchema>()

    private val tagArb = arbitrary { Arb.string(minSize = 3, maxSize = 20, codepoints = Codepoint.az()).bind() }

    @Test
    fun fetchAutocomplete() = runTest {
        val (holder, sortedTags) = openDatabase()
        val repository = repository(holder)

        checkAll(timeLimit, Arb.element(sortedTags.keys), Arb.int(1..5)) { selectedTag, firstLetters ->
            val prompt = selectedTag.take(firstLetters)
            val result = repository.fetchTagAutocomplete(prompt)
            result shouldHaveAtMostSize 6
            result.forEach { tag ->
                tag shouldStartWith prompt
            }
            result shouldBeSortedWith { a, b -> -sortedTags.getValue(a).compareTo(sortedTags.getValue(b)) }
        }
    }

    @Test
    fun fetchAutocomplete_empty() = runTest {
        val (holder, _) = openDatabase()
        val repository = repository(holder)
        val result = repository.fetchTagAutocomplete("")
        result.shouldBeEmpty()
    }

    private fun repository(holder: DatabaseHolder): TagRepository = TagRepository(
        holder,
    )

    private suspend fun TestScope.openDatabase(): Pair<DatabaseHolder, Map<String, Int>> {
        val holder = TestDatabaseHolder(
            "test_database",
            listOf(tagSchema, tagCountSchema)
        )
        onCleanup {
            holder.deleteDatabase()
        }
        val tags = tagArb.take(50).toList()
        val urls = Arb.domain().take(40).toList()
        fun tagSample() = Arb.subsequence(Arb.shuffle(tags).single()).single()
        holder.database().writeTransaction(tagSchema.storeName) {
            val store = objectStore(tagSchema.storeName)
            urls.forEach { url ->
                tagSample().forEach { tag ->
                    store.put(
                        tagSchema.generate(
                            mapOf(
                                TagSchema.Tag to tag,
                                TagSchema.Url to url,
                            )
                        )
                    )
                }
            }
        }
        holder.database().writeTransaction(tagSchema.storeName, tagCountSchema.storeName) {
            val countStore = objectStore(tagCountSchema.storeName)
            val tagStore = objectStore(tagSchema.storeName)
            tags.forEach { tag ->
                countStore.put(
                    tagCountSchema.generate(
                        mapOf(
                            TagCountSchema.Tag to tag,
                            TagCountSchema.Count to tagStore.index(TagSchema.Tag.name).count(Key(tag))
                        )
                    )
                )
            }
        }
        val sortedTags = holder.database().transaction(tagSchema.storeName) {
            val store = objectStore(tagSchema.storeName)
            tags.associateWith { tag ->
                store.index(TagSchema.Tag.name).count(Key(tag))
            }
                .filterValues { it > 0 }
        }
        return Pair(holder, sortedTags)
    }
}