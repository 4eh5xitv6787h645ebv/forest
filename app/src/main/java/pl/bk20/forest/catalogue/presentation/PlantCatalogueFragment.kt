package pl.bk20.forest.catalogue.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import pl.bk20.forest.R
import pl.bk20.forest.databinding.FragmentPlantCatalogueBinding

class PlantCatalogueFragment : Fragment() {

    private val viewModel: PlantCatalogueViewModel by viewModels { PlantCatalogueViewModel.Factory }

    private var _binding: FragmentPlantCatalogueBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlantAdapter

    private val searchWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) { viewModel.setQuery(s?.toString().orEmpty()) }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlantCatalogueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlantAdapter { plant ->
            findNavController().navigate(
                R.id.plantDetailFragment,
                bundleOf(PlantDetailFragment.ARG_PLANT_ID to plant.id),
            )
        }
        binding.recyclerPlants.adapter = adapter
        binding.recyclerPlants.layoutManager = GridLayoutManager(requireContext(), GRID_COLUMNS)
        binding.editSearch.addTextChangedListener(searchWatcher)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect(::render)
            }
        }
    }

    override fun onDestroyView() {
        _binding?.recyclerPlants?.adapter = null
        _binding?.editSearch?.removeTextChangedListener(searchWatcher)
        _binding = null
        super.onDestroyView()
    }

    private fun render(state: PlantCatalogueState) {
        val b = _binding ?: return
        b.progressLoading.visibility = if (state.loading) View.VISIBLE else View.GONE
        b.recyclerPlants.visibility = if (state.loading) View.GONE else View.VISIBLE
        b.textCountLabel.text = getString(
            R.string.catalogue_visible_count,
            state.visible.size, state.totalCount,
        )
        b.textEmpty.visibility =
            if (!state.loading && state.visible.isEmpty()) View.VISIBLE else View.GONE
        adapter.submitList(state.visible)
    }

    private companion object {
        const val GRID_COLUMNS = 2
    }
}
