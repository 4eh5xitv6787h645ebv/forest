package pl.bk20.forest.catalogue.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.bk20.forest.catalogue.domain.model.Plant
import pl.bk20.forest.catalogue.domain.model.PlantHabit

class FilterPlantsTest {

    private fun plant(
        id: Int,
        scientificName: String,
        family: String = "Fabaceae",
        habit: PlantHabit = PlantHabit.SHRUB,
        commonName: String? = null,
    ): Plant {
        val genus = scientificName.substringBefore(' ')
        return Plant(
            id = id,
            scientificName = scientificName,
            commonName = commonName,
            genus = genus,
            epithet = scientificName.substringAfter(' '),
            family = family,
            habit = habit,
            flowerColor = "yellow",
            foliageColor = "green",
            heightMinMeters = 1.0,
            heightMaxMeters = 3.0,
            regions = listOf("NSW"),
            wikipediaUrl = "https://en.wikipedia.org/wiki/$genus",
            drawableResName = "plant_${scientificName.lowercase().replace(' ', '_')}",
            drawableResId = 0,
            searchHaystack = "$scientificName ${commonName.orEmpty()} $genus $family".lowercase(),
        )
    }

    private val plants = listOf(
        plant(1, "Acacia pycnantha", commonName = "Golden Wattle"),
        plant(2, "Eucalyptus regnans", family = "Myrtaceae", habit = PlantHabit.TREE,
            commonName = "Mountain Ash"),
        plant(3, "Banksia serrata", family = "Proteaceae", habit = PlantHabit.SHRUB_LARGE),
        plant(4, "Grevillea robusta", family = "Proteaceae", habit = PlantHabit.TREE),
    )

    @Test
    fun emptyFilters_returnEverything() {
        assertEquals(plants, FilterPlants(plants, "", null, null))
    }

    @Test
    fun queryMatchesScientificName() {
        val result = FilterPlants(plants, "regnans", null, null)
        assertEquals(listOf(plants[1]), result)
    }

    @Test
    fun queryMatchesCommonName() {
        val result = FilterPlants(plants, "Golden", null, null)
        assertEquals(listOf(plants[0]), result)
    }

    @Test
    fun queryIsCaseInsensitive() {
        val result = FilterPlants(plants, "MOUNTAIN", null, null)
        assertEquals(listOf(plants[1]), result)
    }

    @Test
    fun familyFilterFiltersByFamily() {
        val result = FilterPlants(plants, "", "Proteaceae", null)
        assertEquals(listOf(plants[2], plants[3]), result)
    }

    @Test
    fun habitFilterFiltersByHabit() {
        val result = FilterPlants(plants, "", null, PlantHabit.TREE.code)
        assertEquals(listOf(plants[1], plants[3]), result)
    }

    @Test
    fun combinedQueryAndFiltersIntersect() {
        val result = FilterPlants(plants, "Grevillea", "Proteaceae", PlantHabit.TREE.code)
        assertEquals(listOf(plants[3]), result)
    }

    @Test
    fun queryWithNoMatchesReturnsEmpty() {
        val result = FilterPlants(plants, "zzzz", null, null)
        assertTrue(result.isEmpty())
    }

    @Test
    fun blankQueryTreatedAsEmpty() {
        val result = FilterPlants(plants, "   ", null, null)
        assertEquals(plants, result)
    }
}
