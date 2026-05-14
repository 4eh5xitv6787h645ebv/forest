package pl.bk20.forest.catalogue.domain.usecase

import pl.bk20.forest.catalogue.domain.model.Plant
import pl.bk20.forest.catalogue.domain.repository.PlantRepository

interface GetPlantById {
    suspend operator fun invoke(id: Int): Plant?
}

class GetPlantByIdImpl(private val repository: PlantRepository) : GetPlantById {
    override suspend fun invoke(id: Int): Plant? = repository.findById(id)
}
