package pl.bk20.forest.catalogue.data.source

import org.junit.Assert.assertEquals
import org.junit.Test

class PlantJsonSourceTest {

    @Test
    fun drawableName_lowercasesGenusSpecies() {
        assertEquals(
            "plant_acacia_pycnantha",
            PlantJsonSource.drawableNameOf("Acacia pycnantha"),
        )
    }

    @Test
    fun drawableName_replacesHyphenWithUnderscore() {
        assertEquals(
            "plant_acacia_pendulina_form_a",
            PlantJsonSource.drawableNameOf("Acacia pendulina-form-a"),
        )
    }

    @Test
    fun drawableName_stripsApostrophes() {
        assertEquals(
            "plant_grevillea_hookeri",
            PlantJsonSource.drawableNameOf("Grevillea Hooker'i"),
        )
    }
}
