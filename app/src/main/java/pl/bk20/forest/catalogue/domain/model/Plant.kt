package pl.bk20.forest.catalogue.domain.model

/**
 * Immutable plant record. [drawableResId] is precomputed once during parsing
 * so RecyclerView binds avoid expensive `Resources.getIdentifier` lookups.
 * [searchHaystack] is also precomputed (lowercased name/family/genus joined)
 * to keep keystroke filtering cheap.
 */
data class Plant(
    val id: Int,
    val scientificName: String,
    val commonName: String?,
    val genus: String,
    val epithet: String,
    val family: String,
    val habit: PlantHabit,
    val flowerColor: String,
    val foliageColor: String,
    val heightMinMeters: Double,
    val heightMaxMeters: Double,
    val regions: List<String>,
    val wikipediaUrl: String,
    val drawableResName: String,
    val drawableResId: Int,
    val searchHaystack: String,
)
