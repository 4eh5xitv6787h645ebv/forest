package pl.bk20.forest.catalogue.domain.usecase

import pl.bk20.forest.catalogue.domain.model.Plant
import pl.bk20.forest.catalogue.domain.repository.PlantRepository

interface LoadPlants {
    suspend operator fun invoke(): List<Plant>
}

class LoadPlantsImpl(private val repository: PlantRepository) : LoadPlants {
    override suspend fun invoke(): List<Plant> = repository.loadAll()
}
