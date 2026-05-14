package pl.bk20.forest.catalogue.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import pl.bk20.forest.catalogue.domain.model.Plant

/**
 * Maps a non-negative slot number to a bundled plant drawable resource id.
 *
 * Held by [pl.bk20.forest.ForestApplication] as an application-scoped
 * singleton. The application pre-warms it from `Dispatchers.IO` at startup so
 * [drawableForSlot] is a synchronous array lookup at render time — no JSON
 * parse on the main thread.
 *
 * The slot table is stable: slot N always returns the same plant once
 * prepared, so users see a consistent forest as they earn rewards.
 *
 * [readiness] flips from `false` to `true` exactly once after [prepare];
 * callers can observe it to refresh their UI if they happened to render
 * before the pre-warm completed.
 */
class PlantSlots {

    @Volatile private var ids: IntArray = IntArray(0)
    private val _readiness = MutableStateFlow(false)
    val readiness: StateFlow<Boolean> = _readiness.asStateFlow()

    /** Populate the slot table from the parsed plant list. Idempotent. */
    fun prepare(plants: List<Plant>) {
        ids = IntArray(plants.size) { plants[it].drawableResId }
        _readiness.value = true
    }

    /** Return the drawable id for [slot], or [fallbackResId] until prepared. */
    fun drawableForSlot(slot: Int, fallbackResId: Int): Int {
        val cur = ids
        if (cur.isEmpty()) return fallbackResId
        return cur[Math.floorMod(slot, cur.size)]
    }

    fun isPrepared(): Boolean = ids.isNotEmpty()
}
