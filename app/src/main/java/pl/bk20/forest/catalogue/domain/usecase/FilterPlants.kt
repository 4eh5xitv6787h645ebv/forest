package pl.bk20.forest.catalogue.domain.usecase

import pl.bk20.forest.catalogue.domain.model.Plant

/**
 * Pure function — easy to unit-test without an Android context.
 * Uses each plant's precomputed lowercase [Plant.searchHaystack] to keep the
 * per-character cost low even on 1000-entry lists.
 */
object FilterPlants {

    operator fun invoke(
        plants: List<Plant>,
        query: String,
        familyFilter: String?,
        habitFilter: String?,
    ): List<Plant> {
        val needle = query.trim().lowercase()
        if (needle.isEmpty() && familyFilter == null && habitFilter == null) return plants
        return plants.filter { plant ->
            (familyFilter == null || plant.family == familyFilter) &&
                (habitFilter == null || plant.habit.code == habitFilter) &&
                (needle.isEmpty() || plant.searchHaystack.contains(needle))
        }
    }
}
