package pl.bk20.forest.catalogue.domain.model

import android.util.Log
import pl.bk20.forest.BuildConfig

enum class PlantHabit(val code: String) {
    TREE("tree"),
    MALLEE("mallee"),
    SHRUB_LARGE("shrub_large"),
    SHRUB("shrub"),
    SHRUB_SMALL("shrub_small"),
    GROUNDCOVER("groundcover"),
    CLIMBER("climber"),
    GRASS("grass"),
    SEDGE("sedge"),
    GRASS_TREE("grass_tree"),
    ORCHID("orchid"),
    HERB("herb"),
    HERB_LARGE("herb_large"),
    FERN("fern"),
    CYCAD("cycad"),
    PALM("palm");

    companion object {
        private const val TAG = "PlantHabit"
        private val BY_CODE = values().associateBy(PlantHabit::code)

        /** Map a JSON code to an enum value, defaulting to [SHRUB]. */
        fun fromCode(code: String): PlantHabit {
            val resolved = BY_CODE[code]
            if (resolved == null) {
                Log.w(TAG, "Unknown habit code: '$code' — defaulting to SHRUB")
                check(!BuildConfig.DEBUG) {
                    "Unknown plant habit code in plants.json: '$code'"
                }
                return SHRUB
            }
            return resolved
        }
    }
}
