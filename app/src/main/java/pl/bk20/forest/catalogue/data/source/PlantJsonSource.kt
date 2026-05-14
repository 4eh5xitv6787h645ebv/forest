package pl.bk20.forest.catalogue.data.source

import android.content.Context
import android.content.res.Resources
import org.json.JSONArray
import pl.bk20.forest.R
import pl.bk20.forest.catalogue.domain.model.Plant
import pl.bk20.forest.catalogue.domain.model.PlantHabit

/** Parses `res/raw/plants.json` into immutable [Plant] objects.  */
class PlantJsonSource(context: Context) {

    private val resources: Resources = context.resources
    private val packageName: String = context.packageName

    fun read(): List<Plant> {
        val raw = resources.openRawResource(R.raw.plants)
            .use { it.bufferedReader().readText() }
        val arr = JSONArray(raw)
        val fallback = R.drawable.tree_collected
        val result = ArrayList<Plant>(arr.length())
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            val scientificName = o.getString("scientificName")
            val regionsArr = o.getJSONArray("regions")
            val regions = ArrayList<String>(regionsArr.length())
            for (j in 0 until regionsArr.length()) regions += regionsArr.getString(j)
            val commonName = if (o.isNull("commonName")) null else o.getString("commonName")
            val drawableResName = drawableNameOf(scientificName)
            val drawableResId = resources
                .getIdentifier(drawableResName, "drawable", packageName)
                .takeIf { it != 0 } ?: fallback
            val genus = o.getString("genus")
            val family = o.getString("family")
            val searchHaystack = listOfNotNull(
                scientificName, commonName, genus, family
            ).joinToString(" ").lowercase()
            result += Plant(
                id = o.getInt("id"),
                scientificName = scientificName,
                commonName = commonName,
                genus = genus,
                epithet = o.getString("epithet"),
                family = family,
                habit = PlantHabit.fromCode(o.getString("habit")),
                flowerColor = o.getString("flowerColor"),
                foliageColor = o.getString("foliageColor"),
                heightMinMeters = o.getDouble("heightMinMeters"),
                heightMaxMeters = o.getDouble("heightMaxMeters"),
                regions = regions,
                wikipediaUrl = o.getString("wikipediaUrl"),
                drawableResName = drawableResName,
                drawableResId = drawableResId,
                searchHaystack = searchHaystack,
            )
        }
        return result
    }

    companion object {
        fun drawableNameOf(scientificName: String): String =
            "plant_" + scientificName
                .lowercase()
                .replace(' ', '_')
                .replace('-', '_')
                .replace("'", "")
    }
}
