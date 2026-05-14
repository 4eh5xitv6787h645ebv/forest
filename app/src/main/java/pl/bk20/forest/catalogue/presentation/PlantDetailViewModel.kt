package pl.bk20.forest.catalogue.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.bk20.forest.ForestApplication
import pl.bk20.forest.catalogue.domain.model.Plant
import pl.bk20.forest.catalogue.domain.usecase.PlantCatalogueUseCases

class PlantDetailViewModel(
    private val useCases: PlantCatalogueUseCases,
) : ViewModel() {

    private val _plant = MutableStateFlow<Plant?>(null)
    val plant: StateFlow<Plant?> = _plant.asStateFlow()

    fun load(id: Int) {
        viewModelScope.launch { _plant.value = useCases.getPlantById(id) }
    }

    object Factory : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val app = checkNotNull(extras[APPLICATION_KEY]) as ForestApplication
            return PlantDetailViewModel(PlantCatalogueUseCases(app.plantRepository)) as T
        }
    }
}
