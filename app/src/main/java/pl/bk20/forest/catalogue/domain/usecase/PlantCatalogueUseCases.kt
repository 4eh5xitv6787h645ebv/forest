package pl.bk20.forest.catalogue.domain.usecase

import pl.bk20.forest.catalogue.domain.repository.PlantRepository

class PlantCatalogueUseCases(repository: PlantRepository) {
    val loadPlants: LoadPlants = LoadPlantsImpl(repository)
    val getPlantById: GetPlantById = GetPlantByIdImpl(repository)
}
