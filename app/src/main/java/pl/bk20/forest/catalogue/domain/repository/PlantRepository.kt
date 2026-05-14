package pl.bk20.forest.catalogue.domain.repository

import pl.bk20.forest.catalogue.domain.model.Plant

interface PlantRepository {
    suspend fun loadAll(): List<Plant>
    suspend fun findById(id: Int): Plant?
    suspend fun pickStable(slot: Int): Plant
}
