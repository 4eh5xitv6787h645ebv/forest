package pl.bk20.forest.catalogue.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import pl.bk20.forest.catalogue.data.source.PlantJsonSource
import pl.bk20.forest.catalogue.domain.model.Plant
import pl.bk20.forest.catalogue.domain.repository.PlantRepository

/**
 * In-memory repository backed by the bundled plants.json.
 *
 * Designed to be held as an application-scoped singleton (see
 * [pl.bk20.forest.ForestApplication]). A single instance services every
 * ViewModel; the cache survives across screens.
 */
class PlantRepositoryImpl(private val source: PlantJsonSource) : PlantRepository {

    @Volatile private var cache: List<Plant>? = null
    @Volatile private var byId: Map<Int, Plant> = emptyMap()
    private val mutex = Mutex()

    override suspend fun loadAll(): List<Plant> {
        cache?.let { return it }
        return mutex.withLock {
            cache ?: withContext(Dispatchers.IO) {
                val list = source.read()
                byId = list.associateBy(Plant::id)
                list
            }.also { cache = it }
        }
    }

    override suspend fun findById(id: Int): Plant? {
        if (cache == null) loadAll()
        return byId[id]
    }

    override suspend fun pickStable(slot: Int): Plant {
        val all = loadAll()
        check(all.isNotEmpty()) { "plants.json is empty" }
        val index = Math.floorMod(slot, all.size)
        return all[index]
    }
}
