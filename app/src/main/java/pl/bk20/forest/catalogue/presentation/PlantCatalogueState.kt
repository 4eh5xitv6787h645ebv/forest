package pl.bk20.forest.catalogue.presentation

import pl.bk20.forest.catalogue.domain.model.Plant

data class PlantCatalogueState(
    val loading: Boolean = true,
    val plants: List<Plant> = emptyList(),
    val query: String = "",
    val familyFilter: String? = null,
    val habitFilter: String? = null,
    val visible: List<Plant> = emptyList(),
    val totalCount: Int = 0,
)
