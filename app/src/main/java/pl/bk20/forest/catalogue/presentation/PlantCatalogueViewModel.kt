package pl.bk20.forest.catalogue.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.bk20.forest.ForestApplication
import pl.bk20.forest.catalogue.domain.model.Plant
import pl.bk20.forest.catalogue.domain.usecase.FilterPlants
import pl.bk20.forest.catalogue.domain.usecase.PlantCatalogueUseCases

@OptIn(FlowPreview::class)
class PlantCatalogueViewModel(
    private val useCases: PlantCatalogueUseCases,
) : ViewModel() {

    private val plants = MutableStateFlow<List<Plant>>(emptyList())
    private val query = MutableStateFlow("")
    private val familyFilter = MutableStateFlow<String?>(null)
    private val habitFilter = MutableStateFlow<String?>(null)
    private val loading = MutableStateFlow(true)

    // Debounce keystrokes, but let an empty/blank query propagate immediately
    // so 'Clear filters' or programmatic resets feel snappy.
    private val debouncedQuery = query
        .debounce { if (it.isBlank()) 0L else SEARCH_DEBOUNCE_MS }

    val state: StateFlow<PlantCatalogueState> = combine(
        plants, debouncedQuery, familyFilter, habitFilter, loading,
    ) { all, q, fam, hab, isLoading ->
        val visible = FilterPlants(all, q, fam, hab)
        PlantCatalogueState(
            loading = isLoading,
            plants = all,
            query = q,
            familyFilter = fam,
            habitFilter = hab,
            visible = visible,
            totalCount = all.size,
        )
    }
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.Eagerly, PlantCatalogueState())

    init {
        viewModelScope.launch {
            val loaded = useCases.loadPlants()
            plants.value = loaded
            loading.value = false
        }
    }

    fun setQuery(value: String) { query.value = value.trim() }
    fun setFamilyFilter(family: String?) { familyFilter.value = family }
    fun setHabitFilter(habit: String?) { habitFilter.value = habit }
    fun clearFilters() {
        query.value = ""
        familyFilter.value = null
        habitFilter.value = null
    }

    object Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val app = checkNotNull(extras[APPLICATION_KEY]) as ForestApplication
            return PlantCatalogueViewModel(PlantCatalogueUseCases(app.plantRepository)) as T
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 200L
    }
}
